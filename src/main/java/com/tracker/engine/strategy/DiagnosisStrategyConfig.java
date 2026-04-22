package com.tracker.engine.strategy;

import com.tracker.domain.StrategyType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

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
