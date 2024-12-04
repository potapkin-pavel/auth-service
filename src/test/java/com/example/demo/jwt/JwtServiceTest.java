package com.example.demo.jwt;

import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private RedisTokenService redisTokenService;

    private JwtService jwtService;
    private Map<String, String> claims = new HashMap<>();
    private static final String USER_ROLE = "USER";  
    private static final String TEST_USERNAME = "testUser";
    private static final String TEST_EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(redisTokenService);
        claims.put("role", USER_ROLE);
    }

    @Test
    void generateToken_ShouldCreateValidTokenAndSaveToRedis() {
        String token = jwtService.generateToken(TEST_USERNAME, claims);
        assertNotNull(token);
        assertTrue(token.length() > 0);
        verify(redisTokenService).saveToken(eq(token), anyLong());
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnUsername() {
        String token = jwtService.generateToken(TEST_USERNAME, claims);
        when(redisTokenService.isTokenValid(token)).thenReturn(true);
        
        String username = jwtService.validateToken(token);
        assertEquals(TEST_USERNAME, username);
        verify(redisTokenService).isTokenValid(token);
    }

    @Test
    void validateToken_WithRevokedToken_ShouldThrowException() {
        String token = jwtService.generateToken(TEST_USERNAME, claims);
        when(redisTokenService.isTokenValid(token)).thenReturn(false);
        
        assertThrows(IllegalArgumentException.class, () -> {
            jwtService.validateToken(token);
        });
        verify(redisTokenService).isTokenValid(token);
    }

    @Test
    void validateToken_WithInvalidToken_ShouldThrowException() {
        String invalidToken = "invalid.token.here";
        when(redisTokenService.isTokenValid(invalidToken)).thenReturn(true);
        
        assertThrows(MalformedJwtException.class, () -> {
            jwtService.validateToken(invalidToken);
        });
    }

    @Test
    void validateToken_WithModifiedToken_ShouldThrowException() {
        String token = jwtService.generateToken(TEST_USERNAME, claims);
        String modifiedToken = token.substring(0, token.length() - 1) + "X";
        when(redisTokenService.isTokenValid(modifiedToken)).thenReturn(true);
        
        assertThrows(SignatureException.class, () -> {
            jwtService.validateToken(modifiedToken);
        });
    }

    @Test
    void getClaims_ShouldReturnTokenClaims() {
        claims.put("email", TEST_EMAIL);
        String token = jwtService.generateToken(TEST_USERNAME, claims);
        
        Map<String, ?> retrievedClaims = jwtService.getClaims(token);
        
        assertEquals(USER_ROLE, retrievedClaims.get("role"));
        assertEquals(TEST_EMAIL, retrievedClaims.get("email"));
        assertEquals(TEST_USERNAME, retrievedClaims.get("sub")); // sub is the subject/username
    }

    @Test
    void revokeToken_ShouldCallRedisService() {
        String token = jwtService.generateToken(TEST_USERNAME, claims);
        jwtService.revokeToken(token);
        verify(redisTokenService).revokeToken(token);
    }
}
