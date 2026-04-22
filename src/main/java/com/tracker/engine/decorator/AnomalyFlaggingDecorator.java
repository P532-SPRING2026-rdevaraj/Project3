package com.tracker.engine.decorator;

import com.tracker.domain.Measurement;
import com.tracker.domain.Observation;
import com.tracker.domain.PhenomenonType;

public class AnomalyFlaggingDecorator extends ObservationProcessorDecorator {

    public AnomalyFlaggingDecorator(ObservationProcessor delegate) {
        super(delegate);
    }

    @Override
    public Observation process(Observation observation) {
        if (observation instanceof Measurement m) {
            PhenomenonType pt = m.getPhenomenonType();
            Double min = pt.getNormalMin();
            Double max = pt.getNormalMax();
            if (min != null || max != null) {
                double amount = m.getAmount();
                boolean anomalous = (min != null && amount < min) || (max != null && amount > max);
                if (anomalous) {
                    m.setAnomalyFlag(true);
                }
            }
        }
        return delegate.process(observation);
    }
}
