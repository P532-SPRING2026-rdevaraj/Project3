package com.tracker.engine.decorator;

import com.tracker.domain.Observation;

public interface ObservationProcessor {

    Observation process(Observation observation);
}
