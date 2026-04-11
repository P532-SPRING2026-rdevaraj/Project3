package com.tracker.dto;

import java.util.List;

/** Request body for creating a diagnostic rule (AssociativeFunction). */
public class AssociativeFunctionRequest {
    private String name;
    /** IDs of PhenomenonTypes that must all be present for the rule to fire. */
    private List<Long> argumentConceptIds;
    /** ID of the PhenomenonType that is inferred when the rule fires. */
    private Long productConceptId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Long> getArgumentConceptIds() { return argumentConceptIds; }
    public void setArgumentConceptIds(List<Long> argumentConceptIds) { this.argumentConceptIds = argumentConceptIds; }

    public Long getProductConceptId() { return productConceptId; }
    public void setProductConceptId(Long productConceptId) { this.productConceptId = productConceptId; }
}
