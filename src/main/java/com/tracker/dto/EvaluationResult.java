package com.tracker.dto;

import com.tracker.domain.StrategyType;

import java.util.List;

/**
 * Response DTO for the "Evaluate rules" endpoint (Change 1).
 * Returns the inferred concept, which strategy fired it, and contributing observation IDs.
 */
public class EvaluationResult {

    private String inferredConcept;
    private StrategyType strategyUsed;
    private List<Long> evidenceObservationIds;

    public EvaluationResult(String inferredConcept, StrategyType strategyUsed,
                            List<Long> evidenceObservationIds) {
        this.inferredConcept = inferredConcept;
        this.strategyUsed = strategyUsed;
        this.evidenceObservationIds = evidenceObservationIds;
    }

    public String getInferredConcept() { return inferredConcept; }
    public StrategyType getStrategyUsed() { return strategyUsed; }
    public List<Long> getEvidenceObservationIds() { return evidenceObservationIds; }
}
