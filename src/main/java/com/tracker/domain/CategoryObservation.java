package com.tracker.domain;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Operational-level entity for a qualitative observation.
 * Links to a knowledge-level Phenomenon and records PRESENT or ABSENT.
 * F4: Record a category observation.
 *
 * All instances must be created via ObservationFactory (Factory pattern).
 * Controllers must never call new CategoryObservation(...) directly.
 */
@Entity
@Table(name = "category_observations")
@PrimaryKeyJoinColumn(name = "observation_id")
public class CategoryObservation extends Observation {

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "phenomenon_id", nullable = false)
    private Phenomenon phenomenon;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Presence presence;

    public CategoryObservation() {}

    public CategoryObservation(Patient patient, Instant recordingTime, Instant applicabilityTime,
                                Protocol protocol, Phenomenon phenomenon, Presence presence) {
        super(patient, recordingTime, applicabilityTime, protocol);
        this.phenomenon = phenomenon;
        this.presence = presence;
    }

    public Phenomenon getPhenomenon() { return phenomenon; }
    public void setPhenomenon(Phenomenon phenomenon) { this.phenomenon = phenomenon; }

    public Presence getPresence() { return presence; }
    public void setPresence(Presence presence) { this.presence = presence; }
}
