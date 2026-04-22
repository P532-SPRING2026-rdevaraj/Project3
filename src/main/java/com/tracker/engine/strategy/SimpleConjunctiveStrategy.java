package com.tracker.engine.strategy;

import com.tracker.domain.*;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
