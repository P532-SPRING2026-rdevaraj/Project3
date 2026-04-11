package com.tracker.engine.command;

/**
 * Command pattern — base interface for all state-changing operations.
 *
 * Each concrete command wraps one user action (record observation, reject
 * observation, create patient) and implements execute().
 *
 * The CommandLog stores every executed command with a timestamp and user.
 * Payloads are persisted as JSON strings so the Week 2 undo path can
 * reconstruct the original request without a schema change.
 */
public interface Command {

    /** Executes the wrapped operation and returns a JSON-serialisable payload. */
    void execute();

    /** Human-readable type label stored in CommandLogEntry.commandType. */
    String getCommandType();

    /** JSON string describing the operation inputs (stored in CommandLogEntry.payload). */
    String getPayload();
}
