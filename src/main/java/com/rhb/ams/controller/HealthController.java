package com.rhb.ams.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.jdbc.health.DataSourceHealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    /**
     * Get basic health status
     *
     * @return Health status response with HTTP 200
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> healthResponse = new LinkedHashMap<>();
        healthResponse.put("status", "UP");
        healthResponse.put("timestamp", LocalDateTime.now());
        healthResponse.put("message", "Application is running successfully");
        return ResponseEntity.ok(healthResponse);
    }


}
