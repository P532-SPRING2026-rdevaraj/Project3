package com.tracker.engine.command;

public interface ObservationAwareCommand extends Command {
    Long getAffectedObservationId();
}
