package com.tracker.dto;

import com.tracker.domain.MeasurementKind;
import java.util.Set;

/** Request body for POST /api/phenomenon-types (F2). */
public class PhenomenonTypeRequest {
    private String name;
    private MeasurementKind kind;
    /** For QUANTITATIVE types: allowed units (e.g. "°C", "mg/dL"). */
    private Set<String> allowedUnits;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public MeasurementKind getKind() { return kind; }
    public void setKind(MeasurementKind kind) { this.kind = kind; }

    public Set<String> getAllowedUnits() { return allowedUnits; }
    public void setAllowedUnits(Set<String> allowedUnits) { this.allowedUnits = allowedUnits; }
}
