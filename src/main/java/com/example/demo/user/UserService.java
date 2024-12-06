package com.example.demo.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.user.role.RoleRepository;
import com.example.demo.user.role.Role;
import com.example.demo.exception.InvalidUserDataException;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public boolean userExist(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent() && passwordEncoder.matches(password, user.get().getPassword());
    }

    public UserResponse register(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new InvalidUserDataException("Email and password can not be empty.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new InvalidUserDataException("User with email " + email + " already exists");
        }
        String encodedPassword = passwordEncoder.encode(password);
        User newUser = userRepository.save(new User(email, encodedPassword));
        return UserResponse.fromUser(newUser);
    }

    public User getUserById(Long id) {
        if (id == null) {
            throw new InvalidUserDataException("User ID cannot be null");
        }
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    public User addRoleToUser(Long userId, String roleName) {
        User user = getUserById(userId);
        Role role = roleRepository.findByName(roleName.toUpperCase())
            .orElseThrow(() -> new EntityNotFoundException("Role not found with name: " + roleName));
        user.addRole(role);
        return userRepository.save(user);
    }

    public User removeRoleFromUser(Long userId, String roleName) {
        User user = getUserById(userId);
        Role role = roleRepository.findByName(roleName.toUpperCase())
            .orElseThrow(() -> new EntityNotFoundException("Role not found with name: " + roleName));
        user.removeRole(role);
        return userRepository.save(user);
    }

    public List<Role> getUserRoles(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        return user.getRoles();
    }
}