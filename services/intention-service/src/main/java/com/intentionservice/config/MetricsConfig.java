package com.intentionservice.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    @Bean
    public Counter intentionPlacedCounter() {
        return Counter.builder("intentions.placed")
                .description("Number of intentions placed")
                .register(meterRegistry);
    }
    
    @Bean
    public Counter intentionConfirmedCounter() {
        return Counter.builder("intentions.confirmed")
                .description("Number of intentions confirmed")
                .register(meterRegistry);
    }
    
    @Bean
    public Counter intentionFailedCounter() {
        return Counter.builder("intentions.failed")
                .description("Number of intentions failed")
                .register(meterRegistry);
    }
}