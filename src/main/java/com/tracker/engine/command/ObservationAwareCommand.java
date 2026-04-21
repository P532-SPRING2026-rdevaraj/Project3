package com.tracker.engine.command;

/**
 * Marker interface for commands that operate on an Observation.
 * CommandLog uses this to store the observationId in the log entry
 * so the undo path can find the affected observation (Change 3).
 */
public interface ObservationAwareCommand extends Command {
    Long getAffectedObservationId();
}
