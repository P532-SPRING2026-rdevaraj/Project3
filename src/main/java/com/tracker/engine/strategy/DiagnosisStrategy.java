package com.tracker.engine.strategy;

import com.tracker.domain.AssociativeFunction;
import com.tracker.domain.Observation;

import java.util.List;

/**
 * Strategy pattern — pluggable rule-evaluation algorithm for DiagnosisEngine.
 *
 * The single evaluate() signature already accommodates WeightedScoringStrategy
 * in Week 2 without changing this interface or DiagnosisEngine.
 *
 * Week 1 concrete implementation: SimpleConjunctiveStrategy.
 * Week 2 will add WeightedScoringStrategy without touching existing code.
 */
public interface DiagnosisStrategy {

    /**
     * Returns true if the given rule should fire for the patient's active observations.
     *
     * @param rule               the associative function to evaluate
     * @param patientObservations the patient's current ACTIVE observations
     * @return true if all conditions of the rule are satisfied
     */
    boolean evaluate(AssociativeFunction rule, List<Observation> patientObservations);
}
