package com.tracker.domain;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Observer pattern — persisted record of every observation lifecycle event.
 * Written by AuditLogListener when an ObservationEvent is published.
 */
@Entity
@Table(name = "audit_log_entries")
public class AuditLogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Human-readable description of the event (e.g. "OBSERVATION_CREATED", "OBSERVATION_REJECTED"). */
    @Column(nullable = false)
    private String event;

    @Column
    private Long observationId;

    @Column
    private Long patientId;

    @Column(nullable = false)
    private Instant timestamp;

    /** Optional extra detail (e.g. inferred concepts from RuleEvaluationListener). */
    @Column(columnDefinition = "TEXT")
    private String detail;

    public AuditLogEntry() {}

    public AuditLogEntry(String event, Long observationId, Long patientId, Instant timestamp, String detail) {
        this.event = event;
        this.observationId = observationId;
        this.patientId = patientId;
        this.timestamp = timestamp;
        this.detail = detail;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }

    public Long getObservationId() { return observationId; }
    public void setObservationId(Long observationId) { this.observationId = observationId; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
}
