package com.tracker.engine;

import com.tracker.domain.*;
import com.tracker.dto.EvaluationResult;
import com.tracker.engine.strategy.DiagnosisStrategy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DiagnosisEngine {

    private final Map<StrategyType, DiagnosisStrategy> strategies;

    public DiagnosisEngine(Map<StrategyType, DiagnosisStrategy> strategies) {
        this.strategies = strategies;
    }

    public List<EvaluationResult> evaluate(List<AssociativeFunction> rules,
                                           List<Observation> patientObservations) {

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
