package com.tracker.engine.strategy;

import com.tracker.domain.StrategyType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Strategy pattern factory — builds the Map<StrategyType, DiagnosisStrategy> injected
 * into DiagnosisEngine (Change 1). Adding a new strategy only requires adding an entry here.
 */
@Configuration
public class DiagnosisStrategyConfig {

    @Bean
    public Map<StrategyType, DiagnosisStrategy> diagnosisStrategyMap(
            SimpleConjunctiveStrategy conjunctive,
            WeightedScoringStrategy weighted) {
        return Map.of(
            StrategyType.CONJUNCTIVE, conjunctive,
            StrategyType.WEIGHTED, weighted
        );
    }
}
