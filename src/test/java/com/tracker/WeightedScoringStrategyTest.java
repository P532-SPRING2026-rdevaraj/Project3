package com.tracker;

import com.tracker.domain.*;
import com.tracker.engine.strategy.WeightedScoringStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for WeightedScoringStrategy (Change 1).
 * Covers score threshold logic and INFERRED observation exclusion (Change 4).
 */
@ExtendWith(MockitoExtension.class)
class WeightedScoringStrategyTest {

    private WeightedScoringStrategy strategy;
    private Patient patient;

    @BeforeEach
    void setUp() {
        strategy = new WeightedScoringStrategy();
        patient = new Patient("Test Patient", java.time.LocalDate.of(1990, 1, 1), null);
        patient.setId(1L);
    }

    private PhenomenonType quantType(Long id, String name) {
        PhenomenonType pt = new PhenomenonType(name, MeasurementKind.QUANTITATIVE);
        pt.setId(id);
        return pt;
    }

    private Measurement manualMeasurement(PhenomenonType pt) {
        Measurement m = new Measurement(patient, Instant.now(), Instant.now(), null, pt, 36.0, "°C");
        m.setId((long) (Math.random() * 1000));
        m.setSource(ObservationSource.MANUAL);
        return m;
    }

    private Measurement inferredMeasurement(PhenomenonType pt) {
        Measurement m = new Measurement(patient, Instant.now(), Instant.now(), null, pt, 36.0, "°C");
        m.setSource(ObservationSource.INFERRED);
        return m;
    }

    private AssociativeFunction ruleWithWeights(List<Long> argIds, Map<Long, Double> weights,
                                                 double threshold, PhenomenonType product) {
        AssociativeFunction rule = new AssociativeFunction("test", argIds, product);
        rule.setStrategyType(StrategyType.WEIGHTED);
        rule.setWeightsMap(weights);
        rule.setThreshold(threshold);
        return rule;
    }

    @Test
    void evaluate_scoreExceedsThreshold_returnsTrue() {
        // Arrange
        PhenomenonType pt1 = quantType(1L, "Fever");
        PhenomenonType pt2 = quantType(2L, "Glucose");
        PhenomenonType product = quantType(3L, "DiabetesRisk");
        AssociativeFunction rule = ruleWithWeights(
            List.of(1L, 2L), Map.of(1L, 0.6, 2L, 0.6), 1.0, product);

        List<Observation> obs = List.of(manualMeasurement(pt1), manualMeasurement(pt2));

        // Act
        boolean result = strategy.evaluate(rule, obs);

        // Assert
        assertTrue(result, "Score 1.2 should exceed threshold 1.0");
    }

    @Test
    void evaluate_scoreBelowThreshold_returnsFalse() {
        // Arrange
        PhenomenonType pt1 = quantType(1L, "Fever");
        PhenomenonType pt2 = quantType(2L, "Glucose");
        PhenomenonType product = quantType(3L, "DiabetesRisk");
        AssociativeFunction rule = ruleWithWeights(
            List.of(1L, 2L), Map.of(1L, 0.3, 2L, 0.3), 1.0, product);

        List<Observation> obs = List.of(manualMeasurement(pt1), manualMeasurement(pt2));

        // Act
        boolean result = strategy.evaluate(rule, obs);

        // Assert
        assertFalse(result, "Score 0.6 should not exceed threshold 1.0");
    }

    @Test
    void evaluate_onlyOneArgPresent_partialScore() {
        // Arrange
        PhenomenonType pt1 = quantType(1L, "Fever");
        PhenomenonType product = quantType(3L, "DiabetesRisk");
        AssociativeFunction rule = ruleWithWeights(
            List.of(1L, 2L), Map.of(1L, 0.8, 2L, 0.8), 1.0, product);

        // Only pt1 is observed
        List<Observation> obs = List.of(manualMeasurement(pt1));

        // Act
        boolean result = strategy.evaluate(rule, obs);

        // Assert
        assertFalse(result, "Partial score 0.8 should not exceed threshold 1.0");
    }

    @Test
    void evaluate_inferredObservationsExcluded_doesNotCount() {
        // Arrange: INFERRED observation should not contribute to score
        PhenomenonType pt1 = quantType(1L, "Fever");
        PhenomenonType product = quantType(2L, "Risk");
        AssociativeFunction rule = ruleWithWeights(
            List.of(1L), Map.of(1L, 1.5), 1.0, product);

        List<Observation> obs = List.of(inferredMeasurement(pt1));

        // Act
        boolean result = strategy.evaluate(rule, obs);

        // Assert
        assertFalse(result, "INFERRED observations must not count toward score");
    }

    @Test
    void evaluate_defaultWeightUsedWhenNotConfigured() {
        // Arrange: no explicit weight set, should default to 1.0
        PhenomenonType pt1 = quantType(1L, "Fever");
        PhenomenonType product = quantType(2L, "Risk");
        AssociativeFunction rule = ruleWithWeights(List.of(1L), Map.of(), 0.5, product);

        List<Observation> obs = List.of(manualMeasurement(pt1));

        // Act
        boolean result = strategy.evaluate(rule, obs);

        // Assert
        assertTrue(result, "Default weight 1.0 should exceed threshold 0.5");
    }
}
