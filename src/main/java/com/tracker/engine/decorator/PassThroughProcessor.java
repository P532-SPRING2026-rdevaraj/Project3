package com.tracker.engine.decorator;

import com.tracker.domain.Observation;
import org.springframework.stereotype.Component;

@Component
public class PassThroughProcessor implements ObservationProcessor {

    @Override
    public Observation process(Observation observation) {
        return observation;
    }
}
