package com.tracker.domain;

import jakarta.persistence.*;
import java.time.Instant;

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

    @Column(nullable = false)
    private Instant recordingTime;

    @Column(nullable = false)
    private Instant applicabilityTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "protocol_id")
    private Protocol protocol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ObservationStatus status = ObservationStatus.ACTIVE;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ObservationSource source = ObservationSource.MANUAL;

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

    public boolean isActive() {
        return ObservationStatus.ACTIVE.equals(this.status);
    }
}
