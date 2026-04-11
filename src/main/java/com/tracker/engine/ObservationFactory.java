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
        if (!phenomenonType.getAllowedUnits().contains(unit)) {
            throw new IllegalArgumentException(
                "Unit '" + unit + "' is not allowed for '" + phenomenonType.getName()
                + "'. Allowed: " + phenomenonType.getAllowedUnits());
        }

        Instant now = Instant.now(clock);
        Instant appTime = (applicabilityTime != null) ? applicabilityTime : now;

        return new Measurement(patient, now, appTime, protocol, phenomenonType, amount, unit);
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
        if (phenomenon.getPhenomenonType().getKind() != MeasurementKind.QUALITATIVE) {
            throw new IllegalArgumentException(
                "Phenomenon '" + phenomenon.getName() + "' belongs to a non-QUALITATIVE type");
        }

        Instant now = Instant.now(clock);
        Instant appTime = (applicabilityTime != null) ? applicabilityTime : now;

        return new CategoryObservation(patient, now, appTime, protocol, phenomenon, presence);
    }
}
