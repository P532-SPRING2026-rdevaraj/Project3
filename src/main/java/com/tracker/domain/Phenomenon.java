package com.tracker.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Knowledge-level entity representing a qualitative value for a PhenomenonType.
 * Examples: blood group A, blood group B, structural condition: Poor.
 * F2: Phenomenon-type catalogue.
 *
 * Phenomena belong to a PhenomenonType and represent the allowed qualitative
 * values that can be observed (Fowler Analysis Patterns Chapter 3).
 */
@Entity
@Table(name = "phenomena")
public class Phenomenon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "phenomenon_type_id", nullable = false)
    private PhenomenonType phenomenonType;

    public Phenomenon() {}

    public Phenomenon(String name, PhenomenonType phenomenonType) {
        this.name = name;
        this.phenomenonType = phenomenonType;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public PhenomenonType getPhenomenonType() { return phenomenonType; }
    public void setPhenomenonType(PhenomenonType phenomenonType) { this.phenomenonType = phenomenonType; }
}
