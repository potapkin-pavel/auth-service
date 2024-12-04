package com.example.demo.health;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostgresHealthService {

    private final JdbcTemplate jdbcTemplate;

    public PostgresHealthService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getPostgresHealthStatus() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            
            if (result != null && result == 1) {
                return "ok";
            } else {
                return "not ok";
            }
        } catch (Exception e) {
            return "not ok";
        }
    }
}