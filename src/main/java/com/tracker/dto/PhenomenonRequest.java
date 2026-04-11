package com.tracker.dto;

/** Request body for adding a phenomenon to a phenomenon type (F2). */
public class PhenomenonRequest {
    private String name;
    private Long phenomenonTypeId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getPhenomenonTypeId() { return phenomenonTypeId; }
    public void setPhenomenonTypeId(Long phenomenonTypeId) { this.phenomenonTypeId = phenomenonTypeId; }
}
