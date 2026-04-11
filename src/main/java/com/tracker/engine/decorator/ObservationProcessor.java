package com.tracker.engine.decorator;

import com.tracker.domain.Observation;

/**
 * Decorator pattern base interface — defines the process() contract for the
 * observation processing pipeline.
 *
 * Week 1 has a single PassThroughProcessor (no-op).
 * Week 2 will add concrete decorators (e.g. UnitNormalizationDecorator,
 * PropagationDecorator) by wrapping an existing ObservationProcessor
 * without modifying this interface or existing classes.
 */
public interface ObservationProcessor {

    /**
     * Processes the observation and returns it (possibly transformed).
     *
     * @param observation the observation to process
     * @return the processed observation
     */
    Observation process(Observation observation);
}
