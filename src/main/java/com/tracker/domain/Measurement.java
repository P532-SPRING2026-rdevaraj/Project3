package com.tracker.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "measurements")
@PrimaryKeyJoinColumn(name = "observation_id")
public class Measurement extends Observation {

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "phenomenon_type_id", nullable = false)
    private PhenomenonType phenomenonType;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String unit;

    public Measurement() {}

    public Measurement(Patient patient, Instant recordingTime, Instant applicabilityTime,
                       Protocol protocol, PhenomenonType phenomenonType, Double amount, String unit) {
        super(patient, recordingTime, applicabilityTime, protocol);
        this.phenomenonType = phenomenonType;
        this.amount = amount;
        this.unit = unit;
    }

    public PhenomenonType getPhenomenonType() { return phenomenonType; }
    public void setPhenomenonType(PhenomenonType phenomenonType) { this.phenomenonType = phenomenonType; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
