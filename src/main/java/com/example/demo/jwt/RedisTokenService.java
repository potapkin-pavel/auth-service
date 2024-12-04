package com.example.demo.jwt;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class RedisTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String TOKEN_PREFIX = "token:";

    public RedisTokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveToken(String token, long expirationTimeMillis) {
        String key = TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(key, "valid", expirationTimeMillis, TimeUnit.MILLISECONDS);
    }

    public boolean isTokenValid(String token) {
        String key = TOKEN_PREFIX + token;
        String value = redisTemplate.opsForValue().get(key);
        return value != null && value.equals("valid");
    }

    public void revokeToken(String token) {
        String key = TOKEN_PREFIX + token;
        redisTemplate.delete(key);
    }
}