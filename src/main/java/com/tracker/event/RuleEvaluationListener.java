package com.tracker.event;

import com.tracker.domain.*;
import com.tracker.dto.EvaluationResult;
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
 * Observer pattern — Listener 2 of 3.
 * Change 1: uses updated DiagnosisEngine.evaluate() that returns EvaluationResult.
 * Change 4: only MANUAL observations are passed to rule evaluation.
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

        // Exclude INFERRED observations from rule evaluation (Change 4)
        List<Observation> manualActiveObs = observationRepository
            .findByPatientIdAndStatus(patientId, ObservationStatus.ACTIVE)
            .stream()
            .filter(o -> o.getSource() == ObservationSource.MANUAL)
            .collect(Collectors.toList());

        List<AssociativeFunction> rules = ruleRepository.findByActiveTrue();
        List<EvaluationResult> results = diagnosisEngine.evaluate(rules, manualActiveObs);

        if (!results.isEmpty()) {
            String detail = "Rules fired — inferred: " + results.stream()
                .map(r -> r.getInferredConcept() + " [" + r.getStrategyUsed() + "]")
                .collect(Collectors.joining(", "));

            auditLogEntryRepository.save(new AuditLogEntry(
                "RULE_EVALUATION",
                event.getObservation().getId(),
                patientId,
                Instant.now(),
                detail
            ));
        }
    }
}
