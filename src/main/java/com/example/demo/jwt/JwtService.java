package com.example.demo.jwt;

import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {

    private final RedisTokenService redisTokenService;
    private final SecretKey secretKey = Jwts.SIG.HS256.key().build(); // Generates a secret key
    private static final long EXPIRATION_TIME = 3600000L; // 1 hour

    public JwtService(RedisTokenService redisTokenService) {
        this.redisTokenService = redisTokenService;
    }

    public String generateToken(String username, Map<String, ?> claims) {
        String token = Jwts
            .builder()
            .subject(username)
            .claims(claims)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(secretKey)
            .compact();
        redisTokenService.saveToken(token, EXPIRATION_TIME);
        return token;
    }

    public String validateToken(String token) {
        if (!redisTokenService.isTokenValid(token)) {
            throw new IllegalArgumentException("Token has been revoked or expired");
        }
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    public Map<String, ?> getClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public void revokeToken(String token) {
        redisTokenService.revokeToken(token);
    }

}
