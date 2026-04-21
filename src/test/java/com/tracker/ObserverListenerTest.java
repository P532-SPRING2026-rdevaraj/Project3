package com.tracker;

import com.tracker.domain.*;
import com.tracker.engine.DiagnosisEngine;
import com.tracker.engine.strategy.DiagnosisStrategy;
import com.tracker.engine.strategy.SimpleConjunctiveStrategy;
import com.tracker.event.AuditLogListener;
import com.tracker.event.ObservationEvent;
import com.tracker.event.RuleEvaluationListener;
import com.tracker.resourceaccess.AssociativeFunctionRepository;
import com.tracker.resourceaccess.AuditLogEntryRepository;
import com.tracker.resourceaccess.ObservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Observer-pattern listeners.
 * Verifies AuditLogListener and RuleEvaluationListener react correctly to events.
 */
@ExtendWith(MockitoExtension.class)
class ObserverListenerTest {

    @Mock private AuditLogEntryRepository auditLogRepo;
    @Mock private AssociativeFunctionRepository ruleRepo;
    @Mock private ObservationRepository observationRepo;

    private AuditLogListener auditLogListener;
    private RuleEvaluationListener ruleEvaluationListener;
    private DiagnosisEngine diagnosisEngine;

    private Patient patient;

    @BeforeEach
    void setUp() {
        auditLogListener = new AuditLogListener(auditLogRepo);
        SimpleConjunctiveStrategy conjunctive = new SimpleConjunctiveStrategy();
        Map<StrategyType, DiagnosisStrategy> strategyMap = Map.of(StrategyType.CONJUNCTIVE, conjunctive);
        diagnosisEngine = new DiagnosisEngine(strategyMap);
        ruleEvaluationListener = new RuleEvaluationListener(
            ruleRepo, observationRepo, diagnosisEngine, auditLogRepo);

        patient = new Patient("Test Patient", LocalDate.of(1980, 1, 1), null);
        patient.setId(1L);
    }

    // ── AuditLogListener ─────────────────────────────────────────

    @Test
    void auditLogListener_onCreatedEvent_savesCreatedEntry() {
        // Arrange
        Measurement obs = makeMeasurement();
        ObservationEvent event = new ObservationEvent(this, obs, ObservationEvent.Type.CREATED);

        // Act
        auditLogListener.onObservationEvent(event);

        // Assert
        ArgumentCaptor<AuditLogEntry> captor = ArgumentCaptor.forClass(AuditLogEntry.class);
        verify(auditLogRepo).save(captor.capture());
        assertEquals("OBSERVATION_CREATED", captor.getValue().getEvent());
        assertEquals(patient.getId(), captor.getValue().getPatientId());
    }

    @Test
    void auditLogListener_onRejectedEvent_savesRejectedEntry() {
        // Arrange
        Measurement obs = makeMeasurement();
        obs.setStatus(ObservationStatus.REJECTED);
        ObservationEvent event = new ObservationEvent(this, obs, ObservationEvent.Type.REJECTED);

        // Act
        auditLogListener.onObservationEvent(event);

        // Assert
        ArgumentCaptor<AuditLogEntry> captor = ArgumentCaptor.forClass(AuditLogEntry.class);
        verify(auditLogRepo).save(captor.capture());
        assertEquals("OBSERVATION_REJECTED", captor.getValue().getEvent());
    }

    @Test
    void auditLogListener_onCreatedEvent_timestampIsSet() {
        // Arrange
        Measurement obs = makeMeasurement();
        ObservationEvent event = new ObservationEvent(this, obs, ObservationEvent.Type.CREATED);

        // Act
        auditLogListener.onObservationEvent(event);

        // Assert
        ArgumentCaptor<AuditLogEntry> captor = ArgumentCaptor.forClass(AuditLogEntry.class);
        verify(auditLogRepo).save(captor.capture());
        assertNotNull(captor.getValue().getTimestamp());
    }

    // ── RuleEvaluationListener ────────────────────────────────────

    @Test
    void ruleEvaluationListener_noRulesFire_noAuditEntryForRules() {
        // Arrange
        Measurement obs = makeMeasurement();
        ObservationEvent event = new ObservationEvent(this, obs, ObservationEvent.Type.CREATED);

        when(observationRepo.findByPatientIdAndStatus(1L, ObservationStatus.ACTIVE))
            .thenReturn(List.of(obs));
        when(ruleRepo.findByActiveTrue()).thenReturn(List.of());  // no rules

        // Act
        ruleEvaluationListener.onObservationEvent(event);

        // Assert — no RULE_EVALUATION entry saved since nothing fired
        verify(auditLogRepo, never()).save(any(AuditLogEntry.class));
    }

    @Test
    void ruleEvaluationListener_ruleFires_savesRuleEvaluationEntry() {
        // Arrange
        PhenomenonType tempType = quantType(1L, "Body Temperature", Set.of("°C"));
        PhenomenonType inferredType = quantType(99L, "Fever Risk", Set.of());

        Measurement obs = new Measurement(patient, Instant.now(), Instant.now(), null, tempType, 39.0, "°C");
        obs.setId(5L);

        AssociativeFunction rule = new AssociativeFunction();
        rule.setArgumentConceptIdList(List.of(1L));
        rule.setProductConcept(inferredType);
        rule.setName("Fever Rule");
        rule.setActive(true);

        ObservationEvent event = new ObservationEvent(this, obs, ObservationEvent.Type.CREATED);
        when(observationRepo.findByPatientIdAndStatus(anyLong(), any())).thenReturn(List.of(obs));
        when(ruleRepo.findByActiveTrue()).thenReturn(List.of(rule));

        // Act
        ruleEvaluationListener.onObservationEvent(event);

        // Assert — a RULE_EVALUATION entry should be saved
        ArgumentCaptor<AuditLogEntry> captor = ArgumentCaptor.forClass(AuditLogEntry.class);
        verify(auditLogRepo).save(captor.capture());
        assertEquals("RULE_EVALUATION", captor.getValue().getEvent());
        assertTrue(captor.getValue().getDetail().contains("Fever Risk"));
    }

    // ── Helpers ─────────────────────────────────────────────────────

    private Measurement makeMeasurement() {
        PhenomenonType pt = quantType(1L, "Body Temperature", Set.of("°C"));
        Measurement m = new Measurement(patient, Instant.now(), Instant.now(), null, pt, 37.0, "°C");
        m.setId(10L);
        return m;
    }

    private PhenomenonType quantType(Long id, String name, Set<String> units) {
        PhenomenonType pt = new PhenomenonType(name, MeasurementKind.QUANTITATIVE);
        pt.setId(id);
        pt.setAllowedUnits(units);
        return pt;
    }
}
