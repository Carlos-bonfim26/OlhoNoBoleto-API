package com.example.OlhoNoBoleto.components;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class InstantHealthIndicator implements HealthIndicator {
    
    private volatile boolean appReady = false;
    
    @EventListener(ApplicationReadyEvent.class)
    public void onAppReady() {
        appReady = true;
        System.out.println("✅ APLICAÇÃO PRONTA - Health check retornando UP");
    }
    
    @Override
    public Health health() {
        if (appReady) {
            return Health.up().withDetail("status", "READY").build();
        } else {
            return Health.up().withDetail("status", "STARTING").build();
        }
    }
}
