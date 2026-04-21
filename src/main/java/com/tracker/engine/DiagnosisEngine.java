package com.tracker.engine;

import com.tracker.domain.*;
import com.tracker.dto.EvaluationResult;
import com.tracker.engine.strategy.DiagnosisStrategy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Engine layer — encapsulates the replaceable rule-evaluation algorithm.
 *
 * Change 1: now holds a Map<StrategyType, DiagnosisStrategy> injected by Spring
 * so that each AssociativeFunction can select its own strategy at evaluation time.
 * The interface and method signature are unchanged; only the dispatch mechanism changed.
 *
 * Engines must not call each other (architectural constraint).
 */
@Service
public class DiagnosisEngine {

    private final Map<StrategyType, DiagnosisStrategy> strategies;

    /** Spring injects all DiagnosisStrategy beans via a factory map (Change 1). */
    public DiagnosisEngine(Map<StrategyType, DiagnosisStrategy> strategies) {
        this.strategies = strategies;
    }

    /**
     * Evaluates all active associative functions against the patient's current
     * MANUAL observations and returns enriched EvaluationResult objects (Change 1).
     *
     * @param rules               all active associative functions
     * @param patientObservations the patient's ACTIVE observations (MANUAL only filtered inside strategy)
     * @return list of EvaluationResult (inferred concept + strategy used + evidence)
     */
    public List<EvaluationResult> evaluate(List<AssociativeFunction> rules,
                                           List<Observation> patientObservations) {
        // INFERRED observations must never be used as evidence — filter once here
        // so individual strategies stay pure and do not need to know about ObservationSource.
        List<Observation> manualOnly = patientObservations.stream()
            .filter(o -> o.getSource() == ObservationSource.MANUAL)
            .toList();

        List<EvaluationResult> results = new ArrayList<>();
        for (AssociativeFunction rule : rules) {
            StrategyType type = rule.getStrategyType() != null
                ? rule.getStrategyType() : StrategyType.CONJUNCTIVE;
            DiagnosisStrategy strategy = strategies.getOrDefault(type,
                strategies.get(StrategyType.CONJUNCTIVE));

            if (strategy != null && strategy.evaluate(rule, manualOnly)) {
                List<Long> evidenceIds = collectEvidenceIds(rule, manualOnly);
                results.add(new EvaluationResult(
                    rule.getProductConcept().getName(), type, evidenceIds));
            }
        }
        return results;
    }

    /** Collects observation IDs that satisfy the rule's argument concepts. */
    private List<Long> collectEvidenceIds(AssociativeFunction rule,
                                          List<Observation> observations) {
        List<Long> ids = new ArrayList<>();
        for (Long conceptId : rule.getArgumentConceptIdList()) {
            for (Observation obs : observations) {
                Long typeId = null;
                if (obs instanceof Measurement m) typeId = m.getPhenomenonType().getId();
                else if (obs instanceof CategoryObservation c)
                    typeId = c.getPhenomenon().getPhenomenonType().getId();
                if (conceptId.equals(typeId)) { ids.add(obs.getId()); break; }
            }
        }
        return ids;
    }
}
