package com.tracker.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "associative_functions")
public class AssociativeFunction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String argumentConceptIds;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "product_concept_id", nullable = false)
    private PhenomenonType productConcept;

    @Column(nullable = false)
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StrategyType strategyType = StrategyType.CONJUNCTIVE;

    @Column(columnDefinition = "TEXT")
    private String weightsRaw;

    @Column
    private Double threshold = 0.5;

    public AssociativeFunction() {}

    public AssociativeFunction(String name, List<Long> argumentConceptIds, PhenomenonType productConcept) {
        this.name = name;
        this.productConcept = productConcept;
        setArgumentConceptIdList(argumentConceptIds);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getArgumentConceptIds() { return argumentConceptIds; }
    public void setArgumentConceptIds(String argumentConceptIds) { this.argumentConceptIds = argumentConceptIds; }

    public PhenomenonType getProductConcept() { return productConcept; }
    public void setProductConcept(PhenomenonType productConcept) { this.productConcept = productConcept; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public StrategyType getStrategyType() { return strategyType; }
    public void setStrategyType(StrategyType strategyType) { this.strategyType = strategyType; }

    public String getWeightsRaw() { return weightsRaw; }
    public void setWeightsRaw(String weightsRaw) { this.weightsRaw = weightsRaw; }

    public Double getThreshold() { return threshold; }
    public void setThreshold(Double threshold) { this.threshold = threshold; }

    public Map<Long, Double> getWeightsMap() {
        Map<Long, Double> map = new HashMap<>();
        if (weightsRaw == null || weightsRaw.isBlank()) return map;
        for (String pair : weightsRaw.split(",")) {
            String[] parts = pair.trim().split(":");
            if (parts.length == 2) {
                try { map.put(Long.parseLong(parts[0].trim()), Double.parseDouble(parts[1].trim())); }
                catch (NumberFormatException ignored) {}
            }
        }
        return map;
    }

    public void setWeightsMap(Map<Long, Double> weights) {
        if (weights == null || weights.isEmpty()) { this.weightsRaw = null; return; }
        StringBuilder sb = new StringBuilder();
        weights.forEach((id, w) -> { if (sb.length() > 0) sb.append(","); sb.append(id).append(":").append(w); });
        this.weightsRaw = sb.toString();
    }

    public List<Long> getArgumentConceptIdList() {
        List<Long> ids = new ArrayList<>();
        if (argumentConceptIds != null && !argumentConceptIds.isBlank()) {
            for (String s : argumentConceptIds.split(",")) {
                String trimmed = s.trim();
                if (!trimmed.isEmpty()) {
                    ids.add(Long.parseLong(trimmed));
                }
            }
        }
        return ids;
    }

    public void setArgumentConceptIdList(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            this.argumentConceptIds = "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ids.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(ids.get(i));
            }
            this.argumentConceptIds = sb.toString();
        }
    }
}
