package com.tracker.engine.decorator;

import com.tracker.domain.Observation;

import java.time.Clock;
import java.time.Instant;

public class AuditStampingDecorator extends ObservationProcessorDecorator {

    private final Clock clock;

    public AuditStampingDecorator(ObservationProcessor delegate, Clock clock) {
        super(delegate);
        this.clock = clock;
    }

    @Override
    public Observation process(Observation observation) {
        observation.setRecordingTime(Instant.now(clock));
        return delegate.process(observation);
    }
}
