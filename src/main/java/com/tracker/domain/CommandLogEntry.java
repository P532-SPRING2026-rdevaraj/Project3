package com.tracker.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "command_log_entries")
public class CommandLogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String commandType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private Instant executedAt;

    @Column(nullable = false)
    private String user;

    @Column
    private Long observationId;

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
