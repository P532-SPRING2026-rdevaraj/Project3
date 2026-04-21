package com.tracker.engine.strategy;

import com.tracker.domain.*;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Strategy pattern — Week 1 concrete implementation (unchanged in Week 2).
 *
 * Fires a rule ONLY when ALL of the rule's argument observation concepts are
 * currently present as MANUAL active observations for the patient.
 *
 * INFERRED observations are excluded to avoid circular inference chains (Change 4).
 * DiagnosisStrategyConfig maps this bean to StrategyType.CONJUNCTIVE.
 */
@Component
public class SimpleConjunctiveStrategy implements DiagnosisStrategy {

    @Override
    public boolean evaluate(AssociativeFunction rule, List<Observation> patientObservations) {
        Set<Long> coveredTypeIds = new HashSet<>();
        for (Observation obs : patientObservations) {
            if (obs instanceof Measurement m) {
                coveredTypeIds.add(m.getPhenomenonType().getId());
            } else if (obs instanceof CategoryObservation c) {
                coveredTypeIds.add(c.getPhenomenon().getPhenomenonType().getId());
            }
        }
        List<Long> required = rule.getArgumentConceptIdList();
        return !required.isEmpty() && coveredTypeIds.containsAll(required);
    }
}
