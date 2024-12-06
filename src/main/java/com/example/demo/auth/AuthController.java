package com.example.demo.auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.jwt.JwtService;
import com.example.demo.user.UserService;
import com.example.demo.user.role.Role;

import java.util.List;

@RestController
@RequestMapping("/v1/auth/")
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest) {
        if (userService.userExist(loginRequest.email(), loginRequest.password())) {
            Map<String, Object> claims = new HashMap<>();
            List<Role> roles = userService.getUserRoles(loginRequest.email());
            claims.put("roles", roles);
            String token = jwtService.generateToken(loginRequest.email(), claims);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body(null);
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validate(@RequestParam String token) {
        try { // TODO: return id and claims
            String username = jwtService.validateToken(token);
            return ResponseEntity.ok("Token is valid for user: " + username);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }
    }

    @GetMapping("/revoke")
    public ResponseEntity<?> revokeToken(@RequestParam String token) {
        jwtService.revokeToken(token);
        return ResponseEntity.ok("Token revoked successfully");
    }

}
