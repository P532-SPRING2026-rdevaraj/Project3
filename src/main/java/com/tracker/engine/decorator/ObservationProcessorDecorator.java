package com.tracker.engine.decorator;

import com.tracker.domain.Observation;

/**
 * Decorator pattern — abstract base for all observation processing decorators.
 *
 * Holds a delegate and forwards process() to it by default.
 * Concrete decorators override only the logic they add, leaving all other
 * behaviour intact.
 *
 * Adding a new decorator in Week 2 means:
 *   1. Extend this class.
 *   2. Override process() with the new logic + super.process(observation).
 *   3. Wire it in via Spring — zero changes to existing code.
 */
public abstract class ObservationProcessorDecorator implements ObservationProcessor {

    protected final ObservationProcessor delegate;

    protected ObservationProcessorDecorator(ObservationProcessor delegate) {
        this.delegate = delegate;
    }

    @Override
    public Observation process(Observation observation) {
        return delegate.process(observation);
    }
}
