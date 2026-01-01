package com.roadmapsh.urlshortener.controllers;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;

@RestController
@RequestMapping("/monitor")
public class MonitorController {
    private final Instant startedAt = Instant.ofEpochMilli(ManagementFactory.getRuntimeMXBean().getStartTime());
    private final BuildProperties buildProperties;

    public MonitorController(ObjectProvider<BuildProperties> buildPropertiesProvider) {
        this.buildProperties = buildPropertiesProvider.getIfAvailable();
    }

    @GetMapping
    public ResponseEntity<MonitorStatus> status() {
        Instant now = Instant.now();
        Duration uptime = Duration.between(startedAt, now);
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();

        MonitorStatus status = new MonitorStatus(
                "ONLINE",
                startedAt,
                now,
                uptime,
                buildProperties != null ? buildProperties.getVersion() : "unknown",
                new Resources(usedMemory, maxMemory, runtime.availableProcessors())
        );

        return ResponseEntity.ok(status);
    }

    public record MonitorStatus(
            String status,
            Instant startedAt,
            Instant asOf,
            Duration uptime,
            String version,
            Resources resources
    ) {
    }

    public record Resources(long memoryUsedBytes, long memoryMaxBytes, int availableProcessors) {
    }
}
