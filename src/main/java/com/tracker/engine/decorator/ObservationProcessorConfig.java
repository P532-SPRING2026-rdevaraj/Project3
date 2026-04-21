package com.tracker.engine.decorator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Clock;

/**
 * Decorator pattern — wires the full processing pipeline (Change 2).
 *
 * Pipeline order (outermost → innermost):
 *   AuditStampingDecorator
 *     → AnomalyFlaggingDecorator
 *       → UnitValidationDecorator
 *         → BaseObservationProcessor
 *
 * ObservationManager never needs to change — it injects ObservationProcessor
 * and this config controls what that bean resolves to.
 */
@Configuration
public class ObservationProcessorConfig {

    @Bean
    @Primary
    public ObservationProcessor observationProcessor(BaseObservationProcessor base, Clock clock) {
        return new AuditStampingDecorator(
            new AnomalyFlaggingDecorator(
                new UnitValidationDecorator(base)),
            clock);
    }
}
