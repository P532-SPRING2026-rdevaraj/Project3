package com.tracker.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Knowledge-level entity representing a type of phenomenon that can be observed.
 * Examples: body temperature (QUANTITATIVE), blood group (QUALITATIVE).
 * F2: Phenomenon-type catalogue.
 *
 * This is strictly at the knowledge level — created by staff, rarely changes,
 * and is never created as a side-effect of recording an observation.
 * (Fowler Analysis Patterns Chapter 3)
 */
@Entity
@Table(name = "phenomenon_types")
public class PhenomenonType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeasurementKind kind;

    /**
     * Allowed units for QUANTITATIVE types (e.g., "°C", "mg/dL", "mmHg").
     * Stored as a comma-separated string for SQLite compatibility.
     */
    @Column(columnDefinition = "TEXT")
    private String allowedUnitsRaw;

    /** Normal range lower bound for QUANTITATIVE types — used by AnomalyFlaggingDecorator (Change 2). */
    @Column
    private Double normalMin;

    /** Normal range upper bound for QUANTITATIVE types — used by AnomalyFlaggingDecorator (Change 2). */
    @Column
    private Double normalMax;

    /**
     * Phenomena (qualitative values) belong to this type via the Phenomenon entity.
     * Only populated for QUALITATIVE types.
     */
    @OneToMany(mappedBy = "phenomenonType", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Phenomenon> phenomena = new ArrayList<>();

    public PhenomenonType() {}

    public PhenomenonType(String name, MeasurementKind kind) {
        this.name = name;
        this.kind = kind;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public MeasurementKind getKind() { return kind; }
    public void setKind(MeasurementKind kind) { this.kind = kind; }

    public String getAllowedUnitsRaw() { return allowedUnitsRaw; }
    public void setAllowedUnitsRaw(String allowedUnitsRaw) { this.allowedUnitsRaw = allowedUnitsRaw; }

    /**
     * Returns the set of allowed units parsed from the raw comma-separated string.
     */
    public Set<String> getAllowedUnits() {
        Set<String> units = new HashSet<>();
        if (allowedUnitsRaw != null && !allowedUnitsRaw.isBlank()) {
            for (String u : allowedUnitsRaw.split(",")) {
                String trimmed = u.trim();
                if (!trimmed.isEmpty()) {
                    units.add(trimmed);
                }
            }
        }
        return units;
    }

    /**
     * Sets the allowed units from a set, serializing to comma-separated string.
     */
    public void setAllowedUnits(Set<String> units) {
        if (units == null || units.isEmpty()) {
            this.allowedUnitsRaw = null;
        } else {
            this.allowedUnitsRaw = String.join(",", units);
        }
    }

    public Double getNormalMin() { return normalMin; }
    public void setNormalMin(Double normalMin) { this.normalMin = normalMin; }

    public Double getNormalMax() { return normalMax; }
    public void setNormalMax(Double normalMax) { this.normalMax = normalMax; }

    public List<Phenomenon> getPhenomena() { return phenomena; }
    public void setPhenomena(List<Phenomenon> phenomena) { this.phenomena = phenomena; }
}
