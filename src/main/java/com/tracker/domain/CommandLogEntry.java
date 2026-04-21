package com.tracker.domain;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Persisted record of every executed Command object.
 * Command pattern — audit trail of state-changing operations.
 *
 * The payload is stored as a JSON string so the undo path in Week 2 can
 * reconstruct the original request without changing the schema.
 */
@Entity
@Table(name = "command_log_entries")
public class CommandLogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String commandType;

    /** JSON payload capturing the full request so Week 2 undo can replay it. */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private Instant executedAt;

    @Column(nullable = false)
    private String user;

    /** Observation affected by this command — used by the undo path (Change 3). */
    @Column
    private Long observationId;

    /** True once this command has been undone — prevents double-undo (Change 3). */
    @Column(nullable = false)
    private boolean undone = false;

    public CommandLogEntry() {}

    public CommandLogEntry(String commandType, String payload, Instant executedAt, String user) {
        this.commandType = commandType;
        this.payload = payload;
        this.executedAt = executedAt;
        this.user = user;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCommandType() { return commandType; }
    public void setCommandType(String commandType) { this.commandType = commandType; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public Instant getExecutedAt() { return executedAt; }
    public void setExecutedAt(Instant executedAt) { this.executedAt = executedAt; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public Long getObservationId() { return observationId; }
    public void setObservationId(Long observationId) { this.observationId = observationId; }

    public boolean isUndone() { return undone; }
    public void setUndone(boolean undone) { this.undone = undone; }
}
