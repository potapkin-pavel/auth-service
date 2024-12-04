package com.example.demo.health;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/health")
public class RedisHealthController {

    private final RedisHealthService redisHealthService;
    private final PostgresHealthService posgresHealthService;

    public RedisHealthController(RedisHealthService redisHealthService, PostgresHealthService posgresHealthService) {
        this.redisHealthService = redisHealthService;
        this.posgresHealthService = posgresHealthService;
    }

    @GetMapping()
    public ResponseEntity<HealthResponse> checkRedisHealth() {
        return ResponseEntity.ok(
            new HealthResponse(
                redisHealthService.getRedisHealthStatus(),
                posgresHealthService.getPostgresHealthStatus()
            )
        );
    }
}
