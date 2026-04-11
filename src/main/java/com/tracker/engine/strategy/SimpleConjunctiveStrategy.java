package com.tracker.engine.strategy;

import com.tracker.domain.AssociativeFunction;
import com.tracker.domain.CategoryObservation;
import com.tracker.domain.Measurement;
import com.tracker.domain.Observation;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Strategy pattern — Week 1 concrete implementation.
 *
 * SimpleConjunctiveStrategy fires a rule ONLY when ALL of the rule's argument
 * observation concepts (PhenomenonType IDs) are currently present as active
 * observations for the patient.
 *
 * Week 2 will add WeightedScoringStrategy as a second @Component without
 * modifying this class or DiagnosisStrategy.
 */
@Component
public class SimpleConjunctiveStrategy implements DiagnosisStrategy {

    @Override
    public boolean evaluate(AssociativeFunction rule, List<Observation> patientObservations) {
        // Collect all PhenomenonType IDs covered by the patient's active observations
        Set<Long> coveredTypeIds = new HashSet<>();
        for (Observation obs : patientObservations) {
            if (obs instanceof Measurement m) {
                coveredTypeIds.add(m.getPhenomenonType().getId());
            } else if (obs instanceof CategoryObservation c) {
                coveredTypeIds.add(c.getPhenomenon().getPhenomenonType().getId());
            }
        }

        // Rule fires only when every argument concept is covered
        List<Long> required = rule.getArgumentConceptIdList();
        return !required.isEmpty() && coveredTypeIds.containsAll(required);
    }
}
