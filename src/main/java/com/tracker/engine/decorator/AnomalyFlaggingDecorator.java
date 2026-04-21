package com.tracker.engine.decorator;

import com.tracker.domain.Measurement;
import com.tracker.domain.Observation;
import com.tracker.domain.PhenomenonType;

/**
 * Decorator pattern — Change 2.
 * Compares the incoming measurement value against the normalMin/normalMax range
 * stored on the phenomenon type. If outside the range, sets anomalyFlag=true
 * on the observation. Anomalous observations are persisted normally; the flag
 * is stored and displayed in the UI.
 */
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
