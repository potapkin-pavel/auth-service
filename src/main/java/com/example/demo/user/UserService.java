package com.example.demo.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.exception.InvalidUserDataException;
import jakarta.persistence.EntityNotFoundException;

import java.util.Optional;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public boolean userExist(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent() && passwordEncoder.matches(password, user.get().getPassword());
    }

    public UserResponse register(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new InvalidUserDataException("Email and password cannot be empty");
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
}