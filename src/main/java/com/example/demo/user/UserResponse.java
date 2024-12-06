package com.example.demo.user;

import java.util.List;

public record UserResponse(
    Long id,
    String email,
    List<String> roles
) {
    public static UserResponse fromUser(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getRoles().stream().map(role -> role.getName()).toList()
        );
    }
}
