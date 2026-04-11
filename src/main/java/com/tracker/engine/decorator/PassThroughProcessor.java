package com.tracker.engine.decorator;

import com.tracker.domain.Observation;
import org.springframework.stereotype.Component;

/**
 * Decorator pattern — Week 1 no-op implementation of ObservationProcessor.
 *
 * Returns the observation unchanged. Week 2 decorators will wrap this bean
 * to add processing stages without modifying this class.
 */
@Component
public class PassThroughProcessor implements ObservationProcessor {

    @Override
    public Observation process(Observation observation) {
        return observation;
    }
}
