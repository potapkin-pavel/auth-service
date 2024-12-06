package com.example.demo.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.user.role.RoleRequest;

@RestController
@RequestMapping("/v1/user")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest registerRequest) {
        UserResponse response = userService.register(registerRequest.email(), registerRequest.password());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(UserResponse.fromUser(user));
    }

    @PostMapping("/{userId}/roles")
    public ResponseEntity<UserResponse> addRole(@PathVariable Long userId, @RequestBody RoleRequest roleRequest) {
        User updatedUser = userService.addRoleToUser(userId, roleRequest.name());
        return ResponseEntity.ok(UserResponse.fromUser(updatedUser));
    }

    @DeleteMapping("/{userId}/roles/{roleName}")
    public ResponseEntity<UserResponse> removeRole(@PathVariable Long userId, @PathVariable String roleName) {
        User updatedUser = userService.removeRoleFromUser(userId, roleName);
        return ResponseEntity.ok(UserResponse.fromUser(updatedUser));
    }
}
