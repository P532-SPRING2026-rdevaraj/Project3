package com.tracker.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Column(columnDefinition = "TEXT")
    private String allowedUnitsRaw;

    @Column
    private Double normalMin;

    @Column
    private Double normalMax;

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
