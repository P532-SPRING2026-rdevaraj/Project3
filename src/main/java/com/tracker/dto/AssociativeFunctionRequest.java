package com.tracker.dto;

import com.tracker.domain.StrategyType;

import java.util.List;
import java.util.Map;

/** Request body for creating a diagnostic rule (AssociativeFunction). Change 1 adds strategy fields. */
public class AssociativeFunctionRequest {
    private String name;
    private List<Long> argumentConceptIds;
    private Long productConceptId;

    /** Which strategy to use for this rule. Defaults to CONJUNCTIVE if omitted (Change 1). */
    private StrategyType strategyType;

    /** Weights per argument concept: conceptId → weight (Change 1). */
    private Map<Long, Double> weightsMap;

    /** Score threshold for WEIGHTED strategy (Change 1). */
    private Double threshold;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Long> getArgumentConceptIds() { return argumentConceptIds; }
    public void setArgumentConceptIds(List<Long> argumentConceptIds) { this.argumentConceptIds = argumentConceptIds; }

    public Long getProductConceptId() { return productConceptId; }
    public void setProductConceptId(Long productConceptId) { this.productConceptId = productConceptId; }

    public StrategyType getStrategyType() { return strategyType; }
    public void setStrategyType(StrategyType strategyType) { this.strategyType = strategyType; }

    public Map<Long, Double> getWeightsMap() { return weightsMap; }
    public void setWeightsMap(Map<Long, Double> weightsMap) { this.weightsMap = weightsMap; }

    public Double getThreshold() { return threshold; }
    public void setThreshold(Double threshold) { this.threshold = threshold; }
}
