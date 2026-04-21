package com.tracker.dto;

import com.tracker.domain.MeasurementKind;
import java.util.Set;

/** Request body for POST /api/phenomenon-types (F2). */
public class PhenomenonTypeRequest {
    private String name;
    private MeasurementKind kind;
    private Set<String> allowedUnits;
    /** Normal range for QUANTITATIVE types — used by AnomalyFlaggingDecorator (Change 2). */
    private Double normalMin;
    private Double normalMax;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public MeasurementKind getKind() { return kind; }
    public void setKind(MeasurementKind kind) { this.kind = kind; }

    public Set<String> getAllowedUnits() { return allowedUnits; }
    public void setAllowedUnits(Set<String> allowedUnits) { this.allowedUnits = allowedUnits; }

    public Double getNormalMin() { return normalMin; }
    public void setNormalMin(Double normalMin) { this.normalMin = normalMin; }

    public Double getNormalMax() { return normalMax; }
    public void setNormalMax(Double normalMax) { this.normalMax = normalMax; }
}
