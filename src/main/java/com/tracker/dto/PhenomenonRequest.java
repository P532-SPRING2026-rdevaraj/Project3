package com.tracker.dto;

/** Request body for adding a phenomenon to a phenomenon type (F2). */
public class PhenomenonRequest {
    private String name;
    private Long phenomenonTypeId;
    /** Optional parent concept for hierarchy (Change 4). */
    private Long parentConceptId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getPhenomenonTypeId() { return phenomenonTypeId; }
    public void setPhenomenonTypeId(Long phenomenonTypeId) { this.phenomenonTypeId = phenomenonTypeId; }

    public Long getParentConceptId() { return parentConceptId; }
    public void setParentConceptId(Long parentConceptId) { this.parentConceptId = parentConceptId; }
}
