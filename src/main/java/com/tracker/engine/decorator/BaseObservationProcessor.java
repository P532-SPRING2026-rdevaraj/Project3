package com.tracker.engine.decorator;

import com.tracker.domain.Observation;
import org.springframework.stereotype.Component;

/**
 * Decorator pattern — base (identity) processor at the bottom of the chain.
 * Returns the observation unchanged; decorators above it add behaviour.
 *
 * Replaces the Week 1 PassThroughProcessor as the innermost element so the
 * decorator chain reads:
 *   AuditStamping → AnomalyFlagging → UnitValidation → Base
 */
@Component
public class BaseObservationProcessor implements ObservationProcessor {

    @Override
    public Observation process(Observation observation) {
        return observation;
    }
}
