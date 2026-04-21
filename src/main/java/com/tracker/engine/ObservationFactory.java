package com.tracker.engine;

import com.tracker.domain.*;
import org.springframework.stereotype.Service;


import java.time.Clock;
import java.time.Instant;

/**
 * Factory pattern — the single authorised constructor for all Observation objects.
 *
 * Validates:
 *   - For Measurement: PhenomenonType must be QUANTITATIVE; unit must be in allowedUnits.
 *   - For CategoryObservation: PhenomenonType of the Phenomenon must be QUALITATIVE;
 *     Phenomenon must belong to a QUALITATIVE PhenomenonType.
 *
 * Controllers must never call new Measurement(...) or new CategoryObservation(...) directly.
 * All validation that can be moved here is here; the Manager trusts factory output as valid.
 */
@Service
public class ObservationFactory {

    private final Clock clock;

    /** Clock is injected so tests can control time deterministically. */
    public ObservationFactory(Clock clock) {
        this.clock = clock;
    }

    /**
     * Creates a validated Measurement.
     *
     * @throws IllegalArgumentException if the phenomenon type is not QUANTITATIVE
     *                                   or the unit is not in the allowed set
     */
    /**
     * Creates a Measurement. Kind validation remains here; unit validation was moved
     * to UnitValidationDecorator in the processing pipeline (Change 2).
     *
     * @throws IllegalArgumentException if the phenomenon type is not QUANTITATIVE
     */
    public Measurement createMeasurement(Patient patient,
                                         PhenomenonType phenomenonType,
                                         Double amount,
                                         String unit,
                                         Protocol protocol,
                                         Instant applicabilityTime) {
        if (phenomenonType.getKind() != MeasurementKind.QUANTITATIVE) {
            throw new IllegalArgumentException(
                "PhenomenonType '" + phenomenonType.getName() + "' is not QUANTITATIVE");
        }

        Instant now = Instant.now(clock);
        Instant appTime = (applicabilityTime != null) ? applicabilityTime : now;

        Measurement m = new Measurement(patient, now, appTime, protocol, phenomenonType, amount, unit);
        m.setSource(ObservationSource.MANUAL);
        return m;
    }

    /**
     * Creates a validated CategoryObservation.
     *
     * @throws IllegalArgumentException if the phenomenon's type is not QUALITATIVE
     */
    public CategoryObservation createCategoryObservation(Patient patient,
                                                          Phenomenon phenomenon,
                                                          Presence presence,
                                                          Protocol protocol,
                                                          Instant applicabilityTime) {
        return createCategoryObservation(patient, phenomenon, presence, protocol,
                                         applicabilityTime, ObservationSource.MANUAL);
    }

    /**
     * Creates a CategoryObservation with an explicit source — used by PropagationListener
     * to create INFERRED observations (Change 4).
     */
    public CategoryObservation createCategoryObservation(Patient patient,
                                                          Phenomenon phenomenon,
                                                          Presence presence,
                                                          Protocol protocol,
                                                          Instant applicabilityTime,
                                                          ObservationSource source) {
        if (phenomenon.getPhenomenonType().getKind() != MeasurementKind.QUALITATIVE) {
            throw new IllegalArgumentException(
                "Phenomenon '" + phenomenon.getName() + "' belongs to a non-QUALITATIVE type");
        }

        Instant now = Instant.now(clock);
        Instant appTime = (applicabilityTime != null) ? applicabilityTime : now;

        CategoryObservation obs = new CategoryObservation(patient, now, appTime, protocol, phenomenon, presence);
        obs.setSource(source);
        return obs;
    }
}
