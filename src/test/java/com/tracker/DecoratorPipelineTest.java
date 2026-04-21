package com.tracker;

import com.tracker.domain.*;
import com.tracker.engine.decorator.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the three Decorator pipeline steps (Change 2).
 * Uses a fixed Clock for deterministic AuditStamping tests.
 */
@ExtendWith(MockitoExtension.class)
class DecoratorPipelineTest {

    private static final Clock FIXED_CLOCK =
        Clock.fixed(Instant.parse("2026-04-19T10:00:00Z"), ZoneId.of("UTC"));

    private Patient patient() {
        Patient p = new Patient("Test", java.time.LocalDate.of(1990, 1, 1), null);
        p.setId(1L);
        return p;
    }

    private PhenomenonType quantType(Set<String> units, Double min, Double max) {
        PhenomenonType pt = new PhenomenonType("Test", MeasurementKind.QUANTITATIVE);
        pt.setId(1L);
        pt.setAllowedUnits(units);
        pt.setNormalMin(min);
        pt.setNormalMax(max);
        return pt;
    }

    private Measurement measurement(PhenomenonType pt, double amount, String unit) {
        return new Measurement(patient(), Instant.now(), Instant.now(), null, pt, amount, unit);
    }

    // ---- UnitValidationDecorator ----

    @Test
    void unitValidation_validUnit_passesThrough() {
        // Arrange
        PhenomenonType pt = quantType(Set.of("°C", "°F"), null, null);
        Measurement m = measurement(pt, 36.5, "°C");
        ObservationProcessor pipeline = new UnitValidationDecorator(new BaseObservationProcessor());

        // Act
        Observation result = pipeline.process(m);

        // Assert
        assertSame(m, result, "Valid unit should pass through unchanged");
    }

    @Test
    void unitValidation_invalidUnit_throwsException() {
        // Arrange
        PhenomenonType pt = quantType(Set.of("°C"), null, null);
        Measurement m = measurement(pt, 36.5, "K");
        ObservationProcessor pipeline = new UnitValidationDecorator(new BaseObservationProcessor());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> pipeline.process(m),
            "Invalid unit should throw IllegalArgumentException");
    }

    @Test
    void unitValidation_nonMeasurementObservation_passesThrough() {
        // Arrange — CategoryObservations are not unit-validated
        PhenomenonType qt = new PhenomenonType("Blood Group", MeasurementKind.QUALITATIVE);
        qt.setId(2L);
        Phenomenon ph = new Phenomenon("A", qt);
        ph.setId(1L);
        CategoryObservation cat = new CategoryObservation(
            patient(), Instant.now(), Instant.now(), null, ph, Presence.PRESENT);
        ObservationProcessor pipeline = new UnitValidationDecorator(new BaseObservationProcessor());

        // Act
        Observation result = pipeline.process(cat);

        // Assert
        assertSame(cat, result);
    }

    // ---- AnomalyFlaggingDecorator ----

    @Test
    void anomalyFlagging_valueInRange_noFlag() {
        // Arrange
        PhenomenonType pt = quantType(Set.of("°C"), 36.0, 38.0);
        Measurement m = measurement(pt, 37.0, "°C");
        ObservationProcessor pipeline = new AnomalyFlaggingDecorator(new BaseObservationProcessor());

        // Act
        pipeline.process(m);

        // Assert
        assertFalse(m.isAnomalyFlag(), "Value within range should not be flagged");
    }

    @Test
    void anomalyFlagging_valueBelowMin_flagged() {
        // Arrange
        PhenomenonType pt = quantType(Set.of("°C"), 36.0, 38.0);
        Measurement m = measurement(pt, 35.0, "°C");
        ObservationProcessor pipeline = new AnomalyFlaggingDecorator(new BaseObservationProcessor());

        // Act
        pipeline.process(m);

        // Assert
        assertTrue(m.isAnomalyFlag(), "Value below normalMin should be flagged");
    }

    @Test
    void anomalyFlagging_valueAboveMax_flagged() {
        // Arrange
        PhenomenonType pt = quantType(Set.of("°C"), 36.0, 38.0);
        Measurement m = measurement(pt, 40.0, "°C");
        ObservationProcessor pipeline = new AnomalyFlaggingDecorator(new BaseObservationProcessor());

        // Act
        pipeline.process(m);

        // Assert
        assertTrue(m.isAnomalyFlag(), "Value above normalMax should be flagged");
    }

    @Test
    void anomalyFlagging_noRangeConfigured_noFlag() {
        // Arrange — no normalMin/normalMax set
        PhenomenonType pt = quantType(Set.of("°C"), null, null);
        Measurement m = measurement(pt, 999.0, "°C");
        ObservationProcessor pipeline = new AnomalyFlaggingDecorator(new BaseObservationProcessor());

        // Act
        pipeline.process(m);

        // Assert
        assertFalse(m.isAnomalyFlag(), "No range configured means no anomaly flag");
    }

    // ---- AuditStampingDecorator ----

    @Test
    void auditStamping_stampsRecordingTime() {
        // Arrange
        PhenomenonType pt = quantType(Set.of("°C"), null, null);
        Measurement m = measurement(pt, 37.0, "°C");
        Instant oldTime = m.getRecordingTime();
        ObservationProcessor pipeline = new AuditStampingDecorator(new BaseObservationProcessor(), FIXED_CLOCK);

        // Act
        pipeline.process(m);

        // Assert
        assertEquals(Instant.parse("2026-04-19T10:00:00Z"), m.getRecordingTime(),
            "AuditStampingDecorator should overwrite recordingTime with fixed clock");
        assertNotEquals(oldTime, m.getRecordingTime());
    }
}
