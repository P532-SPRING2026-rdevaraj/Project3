package com.tracker.engine.strategy;

import com.tracker.domain.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WeightedScoringStrategy implements DiagnosisStrategy {

    private static final double DEFAULT_WEIGHT = 1.0;

    @Override
    public boolean evaluate(AssociativeFunction rule, List<Observation> patientObservations) {
        Map<Long, Double> weights = rule.getWeightsMap();
        double threshold = rule.getThreshold() != null ? rule.getThreshold() : 0.5;

        Map<Long, Boolean> coveredTypeIds = new HashMap<>();
        for (Observation obs : patientObservations) {
            if (obs.getSource() == ObservationSource.INFERRED) continue;
            if (obs instanceof Measurement m) {
                coveredTypeIds.put(m.getPhenomenonType().getId(), true);
            } else if (obs instanceof CategoryObservation c) {
                coveredTypeIds.put(c.getPhenomenon().getPhenomenonType().getId(), true);
            }
        }

        double score = 0.0;
        for (Long conceptId : rule.getArgumentConceptIdList()) {
            if (coveredTypeIds.containsKey(conceptId)) {
                score += weights.getOrDefault(conceptId, DEFAULT_WEIGHT);
            }
        }

        return score > threshold;
    }
}
