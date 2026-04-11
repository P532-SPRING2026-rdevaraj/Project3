package com.tracker.event;

import com.tracker.domain.*;
import com.tracker.engine.DiagnosisEngine;
import com.tracker.resourceaccess.AssociativeFunctionRepository;
import com.tracker.resourceaccess.AuditLogEntryRepository;
import com.tracker.resourceaccess.ObservationRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Observer pattern — Listener 2 of 2.
 *
 * Re-evaluates diagnostic rules for the affected patient whenever an
 * ObservationEvent is published, then logs any newly inferred concepts
 * as an AuditLogEntry with detail text.
 *
 * Decoupled from ObservationManager via Spring events.
 */
@Component
public class RuleEvaluationListener {

    private final AssociativeFunctionRepository ruleRepository;
    private final ObservationRepository observationRepository;
    private final DiagnosisEngine diagnosisEngine;
    private final AuditLogEntryRepository auditLogEntryRepository;

    public RuleEvaluationListener(AssociativeFunctionRepository ruleRepository,
                                  ObservationRepository observationRepository,
                                  DiagnosisEngine diagnosisEngine,
                                  AuditLogEntryRepository auditLogEntryRepository) {
        this.ruleRepository = ruleRepository;
        this.observationRepository = observationRepository;
        this.diagnosisEngine = diagnosisEngine;
        this.auditLogEntryRepository = auditLogEntryRepository;
    }

    @EventListener
    public void onObservationEvent(ObservationEvent event) {
        Long patientId = event.getObservation().getPatient().getId();

        List<Observation> activeObs = observationRepository
            .findByPatientIdAndStatus(patientId, ObservationStatus.ACTIVE);

        List<AssociativeFunction> rules = ruleRepository.findByActiveTrue();

        List<PhenomenonType> inferred = diagnosisEngine.evaluate(rules, activeObs);

        if (!inferred.isEmpty()) {
            String detail = "Rules fired — inferred: " + inferred.stream()
                .map(PhenomenonType::getName)
                .collect(Collectors.joining(", "));

            AuditLogEntry entry = new AuditLogEntry(
                "RULE_EVALUATION",
                event.getObservation().getId(),
                patientId,
                Instant.now(),
                detail
            );
            auditLogEntryRepository.save(entry);
        }
    }
}
