package com.s21.devops.sample.gatewayservice.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MetricsCollector {

    private final Counter gatewayRequests;

    @Autowired
    public MetricsCollector(MeterRegistry meterRegistry) {
        this.gatewayRequests = Counter.builder("gateway_requests_total")
                .description("Total requests to gateway")
                .register(meterRegistry);
    }

    public void incrementRequestReceived() {
        gatewayRequests.increment();
    }
}
