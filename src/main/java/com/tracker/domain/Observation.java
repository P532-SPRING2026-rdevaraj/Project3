package com.tracker.domain;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Abstract operational-level base entity for all observations.
 * Concrete subtypes: Measurement (quantitative) and CategoryObservation (qualitative).
 *
 * Uses JPA JOINED inheritance so each subtype has its own table while sharing
 * the common columns here. This makes adding new subtypes in Week 2 a zero-touch
 * change to this class.
 *
 * F3, F4, F7, F8.
 */
@Entity
@Table(name = "observations")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Observation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    /** System timestamp: when the record was entered into the system. */
    @Column(nullable = false)
    private Instant recordingTime;

    /** Clinical timestamp: when the observation was actually taken (entered by staff). */
    @Column(nullable = false)
    private Instant applicabilityTime;

    /** Optional protocol used when taking this observation. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "protocol_id")
    private Protocol protocol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ObservationStatus status = ObservationStatus.ACTIVE;

    /** Free-text rejection reason. */
    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    /** MANUAL = entered by staff; INFERRED = created by PropagationListener (Change 4). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ObservationSource source = ObservationSource.MANUAL;

    /** Set by AnomalyFlaggingDecorator when value is outside normalMin/normalMax (Change 2). */
    @Column(nullable = false)
    private boolean anomalyFlag = false;

    protected Observation() {}

    protected Observation(Patient patient, Instant recordingTime, Instant applicabilityTime, Protocol protocol) {
        this.patient = patient;
        this.recordingTime = recordingTime;
        this.applicabilityTime = applicabilityTime;
        this.protocol = protocol;
        this.status = ObservationStatus.ACTIVE;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Instant getRecordingTime() { return recordingTime; }
    public void setRecordingTime(Instant recordingTime) { this.recordingTime = recordingTime; }

    public Instant getApplicabilityTime() { return applicabilityTime; }
    public void setApplicabilityTime(Instant applicabilityTime) { this.applicabilityTime = applicabilityTime; }

    public Protocol getProtocol() { return protocol; }
    public void setProtocol(Protocol protocol) { this.protocol = protocol; }

    public ObservationStatus getStatus() { return status; }
    public void setStatus(ObservationStatus status) { this.status = status; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public ObservationSource getSource() { return source; }
    public void setSource(ObservationSource source) { this.source = source; }

    public boolean isAnomalyFlag() { return anomalyFlag; }
    public void setAnomalyFlag(boolean anomalyFlag) { this.anomalyFlag = anomalyFlag; }

    /** Returns true when this observation should participate in rule evaluation. */
    public boolean isActive() {
        return ObservationStatus.ACTIVE.equals(this.status);
    }
}
