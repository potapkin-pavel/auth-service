package com.example.demo.user;

public record UserResponse(
    Long id,
    String email
) {
    public static UserResponse fromUser(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail()
        );
    }
}
