package com.tracker.event;

import com.tracker.domain.Observation;
import org.springframework.context.ApplicationEvent;

/**
 * Observer pattern — domain event published by ObservationManager whenever
 * an observation is created or rejected.
 *
 * Uses Spring's ApplicationEvent / ApplicationEventPublisher so listeners
 * are decoupled. Adding PropagationListener in Week 2 is a zero-touch
 * addition to existing code — no changes to this class or the manager.
 */
public class ObservationEvent extends ApplicationEvent {

    public enum Type {
        CREATED,
        REJECTED
    }

    private final Observation observation;
    private final Type eventType;

    public ObservationEvent(Object source, Observation observation, Type eventType) {
        super(source);
        this.observation = observation;
        this.eventType = eventType;
    }

    public Observation getObservation() {
        return observation;
    }

    public Type getEventType() {
        return eventType;
    }
}
