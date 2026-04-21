package com.tracker;

import com.tracker.domain.*;
import com.tracker.engine.UserContextHolder;
import com.tracker.engine.command.*;
import com.tracker.event.ObservationEvent;
import com.tracker.manager.UndoManager;
import com.tracker.resourceaccess.CommandLogEntryRepository;
import com.tracker.resourceaccess.ObservationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the Week 2 undo extension (Change 3):
 * AuditableCommandLog — records real acting user and observationId.
 * UndoManager — reverses RECORD_OBSERVATION and REJECT_OBSERVATION entries.
 */
@ExtendWith(MockitoExtension.class)
class CommandUndoTest {

    @Mock private CommandLogEntryRepository commandLogEntryRepository;
    @Mock private ObservationRepository observationRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    private static final Instant FIXED_NOW = Instant.parse("2026-04-01T10:00:00Z");

    private AuditableCommandLog auditableCommandLog;
    private UndoManager undoManager;
    private Patient patient;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(FIXED_NOW, ZoneOffset.UTC);
        auditableCommandLog = new AuditableCommandLog(commandLogEntryRepository, fixedClock);
        undoManager = new UndoManager(commandLogEntryRepository, observationRepository, eventPublisher);
        patient = new Patient("Test", LocalDate.of(1990, 1, 1), null);
        patient.setId(1L);
    }

    @AfterEach
    void tearDown() {
        UserContextHolder.clear();
    }

    // ── AuditableCommandLog ───────────────────────────────────────

    @Test
    void auditableCommandLog_execute_recordsCurrentUserFromContext() {
        UserContextHolder.set("dr.jones");
        when(commandLogEntryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PhenomenonType pt = new PhenomenonType("Temp", MeasurementKind.QUANTITATIVE);
        pt.setId(1L);
        Measurement m = new Measurement(patient, FIXED_NOW, FIXED_NOW, null, pt, 37.0, "°C");
        m.setId(10L);
        when(observationRepository.save(any())).thenReturn(m);

        RecordObservationCommand cmd = new RecordObservationCommand(observationRepository, m, "{}");
        auditableCommandLog.execute(cmd);

        ArgumentCaptor<CommandLogEntry> captor = ArgumentCaptor.forClass(CommandLogEntry.class);
        verify(commandLogEntryRepository).save(captor.capture());
        assertEquals("dr.jones", captor.getValue().getUser());
    }

    @Test
    void auditableCommandLog_execute_storesObservationIdViaGetSavedObservation() {
        // AuditableCommandLog must capture the ID from getSavedObservation()
        // without RecordObservationCommand implementing any new interface.
        when(commandLogEntryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PhenomenonType pt = new PhenomenonType("Temp", MeasurementKind.QUANTITATIVE);
        pt.setId(1L);
        Measurement m = new Measurement(patient, FIXED_NOW, FIXED_NOW, null, pt, 37.0, "°C");
        m.setId(42L);
        when(observationRepository.save(any())).thenReturn(m);

        RecordObservationCommand cmd = new RecordObservationCommand(observationRepository, m, "{}");
        auditableCommandLog.execute(cmd);

        ArgumentCaptor<CommandLogEntry> captor = ArgumentCaptor.forClass(CommandLogEntry.class);
        verify(commandLogEntryRepository).save(captor.capture());
        assertEquals(42L, captor.getValue().getObservationId());
    }

    @Test
    void auditableCommandLog_execute_storesObservationIdFromRejectPayload() {
        // RejectObservationCommand's payload already contains observationId (Week 1 behavior).
        // AuditableCommandLog must parse it without any new interface.
        when(commandLogEntryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PhenomenonType pt = new PhenomenonType("Temp", MeasurementKind.QUANTITATIVE);
        Measurement obs = new Measurement(patient, FIXED_NOW, FIXED_NOW, null, pt, 37.0, "°C");
        obs.setId(99L);
        when(observationRepository.save(any())).thenReturn(obs);

        RejectObservationCommand cmd = new RejectObservationCommand(observationRepository, obs, "error");
        auditableCommandLog.execute(cmd);

        ArgumentCaptor<CommandLogEntry> captor = ArgumentCaptor.forClass(CommandLogEntry.class);
        verify(commandLogEntryRepository).save(captor.capture());
        assertEquals(99L, captor.getValue().getObservationId());
    }

    // ── UndoManager — RECORD_OBSERVATION ─────────────────────────

    @Test
    void undoManager_undoRecordObservation_setsRejectedAndFiresEvent() {
        PhenomenonType pt = new PhenomenonType("Temp", MeasurementKind.QUANTITATIVE);
        Measurement obs = new Measurement(patient, FIXED_NOW, FIXED_NOW, null, pt, 37.0, "°C");
        obs.setId(7L);
        obs.setStatus(ObservationStatus.ACTIVE);

        CommandLogEntry entry = new CommandLogEntry("RECORD_OBSERVATION", "{}", FIXED_NOW, "staff");
        entry.setId(1L);
        entry.setObservationId(7L);

        when(commandLogEntryRepository.findById(1L)).thenReturn(Optional.of(entry));
        when(observationRepository.findById(7L)).thenReturn(Optional.of(obs));
        when(commandLogEntryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        undoManager.undoCommand(1L, "staff");

        assertEquals(ObservationStatus.REJECTED, obs.getStatus());
        verify(eventPublisher).publishEvent(any(ObservationEvent.class));
        assertTrue(entry.isUndone());
    }

    @Test
    void undoManager_undoRejectObservation_restoresActiveAndFiresEvent() {
        PhenomenonType pt = new PhenomenonType("Temp", MeasurementKind.QUANTITATIVE);
        Measurement obs = new Measurement(patient, FIXED_NOW, FIXED_NOW, null, pt, 37.0, "°C");
        obs.setId(8L);
        obs.setStatus(ObservationStatus.REJECTED);
        obs.setRejectionReason("mistake");

        CommandLogEntry entry = new CommandLogEntry(
            "REJECT_OBSERVATION", "{\"observationId\":8,\"reason\":\"mistake\"}", FIXED_NOW, "staff");
        entry.setId(2L);
        entry.setObservationId(8L);

        when(commandLogEntryRepository.findById(2L)).thenReturn(Optional.of(entry));
        when(observationRepository.findById(8L)).thenReturn(Optional.of(obs));
        when(commandLogEntryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        undoManager.undoCommand(2L, "staff");

        assertEquals(ObservationStatus.ACTIVE, obs.getStatus());
        assertNull(obs.getRejectionReason());
        verify(eventPublisher).publishEvent(any(ObservationEvent.class));
    }

    @Test
    void undoManager_doubleUndo_throwsIllegalState() {
        CommandLogEntry entry = new CommandLogEntry("RECORD_OBSERVATION", "{}", FIXED_NOW, "staff");
        entry.setId(3L);
        entry.setUndone(true);

        when(commandLogEntryRepository.findById(3L)).thenReturn(Optional.of(entry));

        assertThrows(IllegalStateException.class, () -> undoManager.undoCommand(3L, "staff"));
    }

    @Test
    void undoManager_wrongUser_throwsSecurityException() {
        CommandLogEntry entry = new CommandLogEntry("RECORD_OBSERVATION", "{}", FIXED_NOW, "dr.jones");
        entry.setId(4L);

        when(commandLogEntryRepository.findById(4L)).thenReturn(Optional.of(entry));

        assertThrows(SecurityException.class, () -> undoManager.undoCommand(4L, "other.user"));
    }
}
