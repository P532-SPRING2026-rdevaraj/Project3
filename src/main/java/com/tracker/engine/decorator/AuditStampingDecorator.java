package com.tracker.engine.decorator;

import com.tracker.domain.Observation;

import java.time.Clock;
import java.time.Instant;

/**
 * Decorator pattern — Change 2.
 * Stamps the definitive recording timestamp onto the observation before persistence.
 * The acting user is tracked separately in CommandLog via UserContextHolder.
 */
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
