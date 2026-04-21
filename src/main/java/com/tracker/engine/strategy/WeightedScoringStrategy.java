package com.tracker.engine.strategy;

import com.tracker.domain.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Strategy pattern — Week 2 concrete implementation (Change 1).
 *
 * Fires a rule when the sum of weights of currently PRESENT (MANUAL) argument
 * concepts exceeds the rule's configurable threshold.
 *
 * Each AssociativeFunction stores weights as "conceptId:weight,..." in weightsRaw.
 * If no explicit weight is configured for a concept, a default of 1.0 is used.
 *
 * Only MANUAL observations are counted; INFERRED observations are excluded to
 * avoid circular inference chains (Change 4 requirement).
 */
@Component
public class WeightedScoringStrategy implements DiagnosisStrategy {

    private static final double DEFAULT_WEIGHT = 1.0;

    @Override
    public boolean evaluate(AssociativeFunction rule, List<Observation> patientObservations) {
        Map<Long, Double> weights = rule.getWeightsMap();
        double threshold = rule.getThreshold() != null ? rule.getThreshold() : 0.5;

        // Collect phenomenon type IDs covered by MANUAL active observations only
        Map<Long, Boolean> coveredTypeIds = new HashMap<>();
        for (Observation obs : patientObservations) {
            if (obs.getSource() == ObservationSource.INFERRED) continue;
            if (obs instanceof Measurement m) {
                coveredTypeIds.put(m.getPhenomenonType().getId(), true);
            } else if (obs instanceof CategoryObservation c) {
                coveredTypeIds.put(c.getPhenomenon().getPhenomenonType().getId(), true);
            }
        }

        // Sum weights of argument concepts that are currently present
        double score = 0.0;
        for (Long conceptId : rule.getArgumentConceptIdList()) {
            if (coveredTypeIds.containsKey(conceptId)) {
                score += weights.getOrDefault(conceptId, DEFAULT_WEIGHT);
            }
        }

        return score > threshold;
    }
}
