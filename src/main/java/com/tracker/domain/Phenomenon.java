package com.tracker.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_concept_id")
    private Phenomenon parentConcept;

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

    public Phenomenon getParentConcept() { return parentConcept; }
    public void setParentConcept(Phenomenon parentConcept) { this.parentConcept = parentConcept; }
}
