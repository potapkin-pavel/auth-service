package com.example.demo.user.role;

public record RoleResponse(Long id, String name) {
    
    public static RoleResponse fromRole(Role role) {
        return new RoleResponse(role.getId(), role.getName());
    }
 }
