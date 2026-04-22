package com.tracker.event;

import com.tracker.domain.Observation;
import org.springframework.context.ApplicationEvent;

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
