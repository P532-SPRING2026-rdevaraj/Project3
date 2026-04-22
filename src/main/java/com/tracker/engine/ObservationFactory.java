package com.tracker.engine;

import com.tracker.domain.*;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

@Service
public class ObservationFactory {

    private final Clock clock;

    public ObservationFactory(Clock clock) {
        this.clock = clock;
    }

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

    public CategoryObservation createCategoryObservation(Patient patient,
                                                          Phenomenon phenomenon,
                                                          Presence presence,
                                                          Protocol protocol,
                                                          Instant applicabilityTime) {
        return createCategoryObservation(patient, phenomenon, presence, protocol,
                                         applicabilityTime, ObservationSource.MANUAL);
    }

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
