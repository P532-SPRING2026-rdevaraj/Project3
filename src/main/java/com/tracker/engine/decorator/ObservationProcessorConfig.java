package com.tracker.engine.decorator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Clock;

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
