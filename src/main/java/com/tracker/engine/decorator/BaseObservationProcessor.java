package com.tracker.engine.decorator;

import com.tracker.domain.Observation;
import org.springframework.stereotype.Component;

@Component
public class BaseObservationProcessor implements ObservationProcessor {

    @Override
    public Observation process(Observation observation) {
        return observation;
    }
}
