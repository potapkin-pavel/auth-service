package com.example.demo.health;

import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

@Service
public class RedisHealthService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisHealthService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String getRedisHealthStatus() {
        try {
            String key = "health:check";
            String value = "OK";
            
            redisTemplate.opsForValue().set(key, value);
            String result = redisTemplate.opsForValue().get(key);
            redisTemplate.delete(key);
            
            if (value.equals(result)) {
                return "ok";
            } else {
                return "not ok";
            }
        } catch (Exception e) {
            return "not ok";
        }
    }

}