package com.tracker.engine;

import com.tracker.domain.AssociativeFunction;
import com.tracker.domain.Observation;
import com.tracker.domain.PhenomenonType;
import com.tracker.engine.strategy.DiagnosisStrategy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Engine layer — encapsulates the replaceable rule-evaluation algorithm.
 *
 * DiagnosisEngine delegates to an injected DiagnosisStrategy, keeping the
 * algorithm swappable without touching this class (Strategy pattern).
 *
 * Engines must not call each other (architectural constraint).
 */
@Service
public class DiagnosisEngine {

    private final DiagnosisStrategy strategy;

    /** Spring injects the active DiagnosisStrategy implementation. */
    public DiagnosisEngine(DiagnosisStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Evaluates all active associative functions against the patient's current
     * observations and returns the list of inferred PhenomenonTypes.
     *
     * @param rules               all active associative functions in the system
     * @param patientObservations the patient's ACTIVE observations
     * @return list of inferred PhenomenonType product concepts
     */
    public List<PhenomenonType> evaluate(List<AssociativeFunction> rules,
                                        List<Observation> patientObservations) {
        List<PhenomenonType> inferred = new ArrayList<>();
        for (AssociativeFunction rule : rules) {
            if (strategy.evaluate(rule, patientObservations)) {
                inferred.add(rule.getProductConcept());
            }
        }
        return inferred;
    }
}
