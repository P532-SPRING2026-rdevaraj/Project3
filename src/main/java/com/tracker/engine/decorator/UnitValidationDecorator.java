package com.tracker.engine.decorator;

import com.tracker.domain.Measurement;
import com.tracker.domain.Observation;

/**
 * Decorator pattern — Change 2.
 * Validates that the unit of a Measurement is in the phenomenon type's allowed-unit set.
 * Throws IllegalArgumentException (acting as ValidationException) if not.
 *
 * Unit validation was previously in ObservationFactory; moving it here keeps the
 * factory focused on object construction while this decorator enforces the constraint
 * as a pipeline step before persistence.
 */
public class UnitValidationDecorator extends ObservationProcessorDecorator {

    public UnitValidationDecorator(ObservationProcessor delegate) {
        super(delegate);
    }

    @Override
    public Observation process(Observation observation) {
        if (observation instanceof Measurement m) {
            var allowed = m.getPhenomenonType().getAllowedUnits();
            if (!allowed.isEmpty() && !allowed.contains(m.getUnit())) {
                throw new IllegalArgumentException(
                    "Unit '" + m.getUnit() + "' is not allowed for '"
                    + m.getPhenomenonType().getName()
                    + "'. Allowed: " + allowed);
            }
        }
        return delegate.process(observation);
    }
}
