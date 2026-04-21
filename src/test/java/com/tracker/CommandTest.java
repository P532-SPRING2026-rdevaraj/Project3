package com.tracker;

import com.tracker.domain.*;
import com.tracker.engine.command.*;
import com.tracker.resourceaccess.CommandLogEntryRepository;
import com.tracker.resourceaccess.ObservationRepository;
import com.tracker.resourceaccess.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Command pattern objects.
 * Tests CreatePatientCommand, RecordObservationCommand, RejectObservationCommand,
 * and CommandLog logging behaviour.
 */
@ExtendWith(MockitoExtension.class)
class CommandTest {

    @Mock private PatientRepository patientRepository;
    @Mock private ObservationRepository observationRepository;
    @Mock private CommandLogEntryRepository commandLogEntryRepository;

    private static final Instant FIXED_NOW = Instant.parse("2026-04-01T10:00:00Z");
    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(FIXED_NOW, ZoneOffset.UTC);
    }

    // ── CreatePatientCommand ──────────────────────────────────────

    @Test
    void createPatientCommand_execute_savesPatientViaRepository() {
        Patient saved = new Patient("Alice", LocalDate.of(1990, 1, 1), "Note");
        saved.setId(1L);
        when(patientRepository.save(any(Patient.class))).thenReturn(saved);

        CreatePatientCommand cmd = new CreatePatientCommand(
            patientRepository, "Alice", LocalDate.of(1990, 1, 1), "Note");

        cmd.execute();

        verify(patientRepository, times(1)).save(any(Patient.class));
        assertNotNull(cmd.getCreatedPatient());
        assertEquals("Alice", cmd.getCreatedPatient().getFullName());
    }

    @Test
    void createPatientCommand_getCommandType_returnsCREATE_PATIENT() {
        CreatePatientCommand cmd = new CreatePatientCommand(
            patientRepository, "Bob", LocalDate.of(1985, 6, 15), null);
        assertEquals("CREATE_PATIENT", cmd.getCommandType());
    }

    @Test
    void createPatientCommand_getPayload_containsFullName() {
        CreatePatientCommand cmd = new CreatePatientCommand(
            patientRepository, "Charlie", LocalDate.of(2000, 3, 10), "Some note");
        String payload = cmd.getPayload();
        assertTrue(payload.contains("Charlie"));
        assertTrue(payload.contains("2000-03-10"));
    }

    // ── RecordObservationCommand ──────────────────────────────────

    @Test
    void recordObservationCommand_execute_savesObservation() {
        Patient p = new Patient("Test", LocalDate.now(), null);
        p.setId(1L);
        PhenomenonType pt = new PhenomenonType("Temp", MeasurementKind.QUANTITATIVE);
        pt.setId(1L);
        Measurement m = new Measurement(p, FIXED_NOW, FIXED_NOW, null, pt, 37.0, "°C");
        when(observationRepository.save(any())).thenReturn(m);

        RecordObservationCommand cmd = new RecordObservationCommand(
            observationRepository, m, "{\"patientId\":1}");

        cmd.execute();

        verify(observationRepository, times(1)).save(m);
        assertNotNull(cmd.getSavedObservation());
    }

    @Test
    void recordObservationCommand_getCommandType_returnsRECORD_OBSERVATION() {
        Patient p = new Patient("Test", LocalDate.now(), null);
        PhenomenonType pt = new PhenomenonType("Temp", MeasurementKind.QUANTITATIVE);
        Measurement m = new Measurement(p, FIXED_NOW, FIXED_NOW, null, pt, 37.0, "°C");

        RecordObservationCommand cmd = new RecordObservationCommand(
            observationRepository, m, "{}");

        assertEquals("RECORD_OBSERVATION", cmd.getCommandType());
    }

    // ── RejectObservationCommand ──────────────────────────────────

    @Test
    void rejectObservationCommand_execute_setsStatusRejected() {
        Patient p = new Patient("Test", LocalDate.now(), null);
        p.setId(1L);
        PhenomenonType pt = new PhenomenonType("Temp", MeasurementKind.QUANTITATIVE);
        Measurement obs = new Measurement(p, FIXED_NOW, FIXED_NOW, null, pt, 37.0, "°C");
        obs.setId(10L);
        when(observationRepository.save(any())).thenReturn(obs);

        RejectObservationCommand cmd = new RejectObservationCommand(
            observationRepository, obs, "Entry error");

        cmd.execute();

        assertEquals(ObservationStatus.REJECTED, obs.getStatus());
        assertEquals("Entry error", obs.getRejectionReason());
        verify(observationRepository).save(obs);
    }

    @Test
    void rejectObservationCommand_getPayload_containsObservationId() {
        Patient p = new Patient("Test", LocalDate.now(), null);
        PhenomenonType pt = new PhenomenonType("Temp", MeasurementKind.QUANTITATIVE);
        Measurement obs = new Measurement(p, FIXED_NOW, FIXED_NOW, null, pt, 37.0, "°C");
        obs.setId(42L);

        RejectObservationCommand cmd = new RejectObservationCommand(
            observationRepository, obs, "Wrong unit");

        String payload = cmd.getPayload();
        assertTrue(payload.contains("42"));
        assertTrue(payload.contains("Wrong unit"));
    }

    // ── CommandLog ────────────────────────────────────────────────

    @Test
    void commandLog_execute_persistsEntryToRepository() {
        CommandLog commandLog = new CommandLog(commandLogEntryRepository, fixedClock);
        when(commandLogEntryRepository.save(any(CommandLogEntry.class)))
            .thenAnswer(inv -> inv.getArgument(0));
        Patient saved = new Patient("Dave", LocalDate.now(), null);
        saved.setId(99L);
        when(patientRepository.save(any())).thenReturn(saved);

        CreatePatientCommand cmd = new CreatePatientCommand(
            patientRepository, "Dave", LocalDate.now(), null);

        commandLog.execute(cmd);

        verify(commandLogEntryRepository, times(1)).save(any(CommandLogEntry.class));
    }

    @Test
    void commandLog_execute_logsUserAsStaff() {
        CommandLog commandLog = new CommandLog(commandLogEntryRepository, fixedClock);
        when(patientRepository.save(any())).thenReturn(new Patient("Eve", LocalDate.now(), null));

        CreatePatientCommand cmd = new CreatePatientCommand(
            patientRepository, "Eve", LocalDate.now(), null);

        when(commandLogEntryRepository.save(any(CommandLogEntry.class)))
            .thenAnswer(inv -> {
                CommandLogEntry entry = inv.getArgument(0);
                assertEquals("staff", entry.getUser());
                return entry;
            });

        commandLog.execute(cmd);

        verify(commandLogEntryRepository).save(any(CommandLogEntry.class));
    }
}
