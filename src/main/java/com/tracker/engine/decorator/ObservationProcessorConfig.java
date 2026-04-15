package com.tracker.engine.decorator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Decorator pattern — wiring configuration for the ObservationProcessor pipeline.
 *
 * Week 1: pipeline is a single PassThroughProcessor (no-op).
 *
 * Week 2: add a new decorator here by wrapping the existing chain, e.g.:
 *   return new UnitNormalizationDecorator(base);
 *
 * ObservationManager never needs to change — it always injects ObservationProcessor
 * and this config controls what that resolves to.
 */
@Configuration
public class ObservationProcessorConfig {

    /**
     * Builds the processor pipeline.
     * Week 1: just the pass-through.
     * Week 2: wrap with additional decorators here.
     */
    @Bean
    @Primary
    public ObservationProcessor observationProcessor(PassThroughProcessor base) {
        // Week 1: identity pipeline
        return base;
        // Week 2 example:
        // return new UnitNormalizationDecorator(base);
    }
}
