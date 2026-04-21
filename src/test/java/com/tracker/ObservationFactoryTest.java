package com.tracker;

import com.tracker.domain.*;
import com.tracker.engine.ObservationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ObservationFactory.
 * Uses a fixed Clock so all timestamps are deterministic.
 */
@ExtendWith(MockitoExtension.class)
class ObservationFactoryTest {

    private static final Instant FIXED_NOW = Instant.parse("2026-04-01T10:00:00Z");
    private ObservationFactory factory;
    private Patient patient;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(FIXED_NOW, ZoneOffset.UTC);
        factory = new ObservationFactory(fixedClock);
        patient = new Patient("Jane Doe", java.time.LocalDate.of(1990, 1, 1), "Test patient");
        patient.setId(1L);
    }

    // ── Measurement — happy path ────────────────────────────────────

    @Test
    void createMeasurement_validInput_returnsMeasurement() {
        // Arrange
        PhenomenonType tempType = quantType("Body Temperature", Set.of("°C", "°F"));

        // Act
        Measurement result = factory.createMeasurement(patient, tempType, 36.6, "°C", null, null);

        // Assert
        assertNotNull(result);
        assertEquals(36.6, result.getAmount());
        assertEquals("°C", result.getUnit());
        assertEquals(patient, result.getPatient());
        assertEquals(FIXED_NOW, result.getRecordingTime());
        assertEquals(FIXED_NOW, result.getApplicabilityTime()); // defaults to now
    }

    @Test
    void createMeasurement_customApplicabilityTime_usesProvidedTime() {
        // Arrange
        PhenomenonType tempType = quantType("Blood Glucose", Set.of("mg/dL"));
        Instant customTime = Instant.parse("2026-03-15T08:00:00Z");

        // Act
        Measurement result = factory.createMeasurement(patient, tempType, 100.0, "mg/dL", null, customTime);

        // Assert
        assertEquals(customTime, result.getApplicabilityTime());
        assertEquals(FIXED_NOW, result.getRecordingTime()); // recording is always now
    }

    @Test
    void createMeasurement_statusIsActive() {
        // Arrange
        PhenomenonType tempType = quantType("Blood Pressure", Set.of("mmHg"));

        // Act
        Measurement result = factory.createMeasurement(patient, tempType, 120.0, "mmHg", null, null);

        // Assert
        assertEquals(ObservationStatus.ACTIVE, result.getStatus());
    }

    // ── Measurement — validation failures ──────────────────────────

    @Test
    void createMeasurement_qualitativeType_throwsIllegalArgument() {
        // Arrange
        PhenomenonType qualType = qualType("Blood Group");

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> factory.createMeasurement(patient, qualType, 1.0, "unit", null, null));
        assertTrue(ex.getMessage().contains("not QUANTITATIVE"));
    }

    @Test
    void createMeasurement_unitNotAllowed_factoryCreatesItAnyway() {
        // Unit validation was moved to UnitValidationDecorator (Change 2).
        // The factory no longer rejects invalid units — it trusts the decorator pipeline.
        // Arrange
        PhenomenonType tempType = quantType("Body Temperature", Set.of("°C"));

        // Act — should NOT throw from factory
        Measurement m = factory.createMeasurement(patient, tempType, 98.6, "°F", null, null);

        // Assert — created successfully; decorator would reject this at pipeline time
        assertNotNull(m);
        assertEquals("°F", m.getUnit());
    }

    @Test
    void createMeasurement_validUnit_returnsCorrectSource() {
        // Arrange — factory must always set source=MANUAL (Change 4)
        PhenomenonType tempType = quantType("Temperature", Set.of("°C"));

        // Act
        Measurement m = factory.createMeasurement(patient, tempType, 37.0, "°C", null, null);

        // Assert
        assertEquals(ObservationSource.MANUAL, m.getSource());
    }

    // ── CategoryObservation — happy path ───────────────────────────

    @Test
    void createCategoryObservation_validInput_returnsCategoryObservation() {
        // Arrange
        PhenomenonType bgType = qualType("Blood Group");
        Phenomenon bloodGroupA = new Phenomenon("A", bgType);

        // Act
        CategoryObservation result = factory.createCategoryObservation(
            patient, bloodGroupA, Presence.PRESENT, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(bloodGroupA, result.getPhenomenon());
        assertEquals(Presence.PRESENT, result.getPresence());
        assertEquals(ObservationStatus.ACTIVE, result.getStatus());
        assertEquals(FIXED_NOW, result.getRecordingTime());
    }

    @Test
    void createCategoryObservation_presenceAbsent_storedCorrectly() {
        // Arrange
        PhenomenonType condType = qualType("Structural Condition");
        Phenomenon poor = new Phenomenon("Poor", condType);

        // Act
        CategoryObservation result = factory.createCategoryObservation(
            patient, poor, Presence.ABSENT, null, null);

        // Assert
        assertEquals(Presence.ABSENT, result.getPresence());
    }

    // ── CategoryObservation — validation failure ────────────────────

    @Test
    void createCategoryObservation_quantitativeType_throwsIllegalArgument() {
        // Arrange
        PhenomenonType quantType = quantType("Body Temperature", Set.of("°C"));
        Phenomenon invalidPhenomenon = new Phenomenon("Celsius", quantType);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> factory.createCategoryObservation(patient, invalidPhenomenon, Presence.PRESENT, null, null));
        assertTrue(ex.getMessage().contains("non-QUALITATIVE"));
    }

    // ── Helpers ─────────────────────────────────────────────────────

    private PhenomenonType quantType(String name, Set<String> units) {
        PhenomenonType pt = new PhenomenonType(name, MeasurementKind.QUANTITATIVE);
        pt.setAllowedUnits(units);
        pt.setId(10L);
        return pt;
    }

    private PhenomenonType qualType(String name) {
        PhenomenonType pt = new PhenomenonType(name, MeasurementKind.QUALITATIVE);
        pt.setId(20L);
        return pt;
    }
}
