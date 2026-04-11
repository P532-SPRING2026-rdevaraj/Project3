package com.tracker;

import com.tracker.domain.*;
import com.tracker.engine.strategy.SimpleConjunctiveStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SimpleConjunctiveStrategy.
 * Verifies that a rule fires only when ALL argument concepts are present.
 */
@ExtendWith(MockitoExtension.class)
class SimpleConjunctiveStrategyTest {

    private SimpleConjunctiveStrategy strategy;
    private Patient patient;

    @BeforeEach
    void setUp() {
        strategy = new SimpleConjunctiveStrategy();
        patient = new Patient("Test Patient", java.time.LocalDate.of(1985, 5, 15), null);
        patient.setId(1L);
    }

    @Test
    void evaluate_allArgumentsPresent_returnsTrue() {
        // Arrange
        PhenomenonType typeA = quantType(1L, "Temp");
        PhenomenonType typeB = quantType(2L, "Glucose");
        AssociativeFunction rule = ruleWith(List.of(1L, 2L), typeB);

        Measurement obsA = measurement(patient, typeA, 37.0, "°C");
        Measurement obsB = measurement(patient, typeB, 120.0, "mg/dL");

        // Act
        boolean result = strategy.evaluate(rule, List.of(obsA, obsB));

        // Assert
        assertTrue(result, "Rule must fire when all argument concepts are present");
    }

    @Test
    void evaluate_oneArgumentMissing_returnsFalse() {
        // Arrange
        PhenomenonType typeA = quantType(1L, "Temp");
        PhenomenonType typeC = quantType(3L, "Pressure");
        AssociativeFunction rule = ruleWith(List.of(1L, 2L, 3L), typeC);

        Measurement obsA = measurement(patient, typeA, 37.0, "°C");
        // typeB (ID=2) and typeC (ID=3) observations are missing

        // Act
        boolean result = strategy.evaluate(rule, List.of(obsA));

        // Assert
        assertFalse(result, "Rule must not fire when some argument concepts are absent");
    }

    @Test
    void evaluate_noObservations_returnsFalse() {
        // Arrange
        PhenomenonType typeA = quantType(1L, "Temp");
        AssociativeFunction rule = ruleWith(List.of(1L), typeA);

        // Act
        boolean result = strategy.evaluate(rule, List.of());

        // Assert
        assertFalse(result, "Rule must not fire with empty observations");
    }

    @Test
    void evaluate_ruleWithNoArguments_returnsFalse() {
        // Arrange — a rule that has an empty argument list should never fire
        PhenomenonType typeA = quantType(1L, "Temp");
        AssociativeFunction rule = ruleWith(List.of(), typeA);

        Measurement obsA = measurement(patient, typeA, 37.0, "°C");

        // Act
        boolean result = strategy.evaluate(rule, List.of(obsA));

        // Assert
        assertFalse(result, "Rule with empty arguments must not fire");
    }

    @Test
    void evaluate_qualitativeCategoryObservationCounts() {
        // Arrange — category observation's phenomenon type should satisfy the rule
        PhenomenonType bgType = qualType(5L, "Blood Group");
        Phenomenon bgA = new Phenomenon("A", bgType);

        AssociativeFunction rule = ruleWith(List.of(5L), bgType);

        CategoryObservation catObs = new CategoryObservation(
            patient, Instant.now(), Instant.now(), null, bgA, Presence.PRESENT);

        // Act
        boolean result = strategy.evaluate(rule, List.of(catObs));

        // Assert
        assertTrue(result, "Qualitative category observation should satisfy the rule");
    }

    // ── Helpers ─────────────────────────────────────────────────────

    private PhenomenonType quantType(Long id, String name) {
        PhenomenonType pt = new PhenomenonType(name, MeasurementKind.QUANTITATIVE);
        pt.setId(id);
        pt.setAllowedUnits(Set.of("°C", "mg/dL", "mmHg", "%"));
        return pt;
    }

    private PhenomenonType qualType(Long id, String name) {
        PhenomenonType pt = new PhenomenonType(name, MeasurementKind.QUALITATIVE);
        pt.setId(id);
        return pt;
    }

    private AssociativeFunction ruleWith(List<Long> argIds, PhenomenonType product) {
        AssociativeFunction f = new AssociativeFunction();
        f.setArgumentConceptIdList(argIds);
        f.setProductConcept(product);
        f.setName("Test Rule");
        f.setActive(true);
        return f;
    }

    private Measurement measurement(Patient p, PhenomenonType pt, double amount, String unit) {
        return new Measurement(p, Instant.now(), Instant.now(), null, pt, amount, unit);
    }
}
