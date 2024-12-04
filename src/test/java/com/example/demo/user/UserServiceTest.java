package com.example.demo.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.exception.InvalidUserDataException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private UserRepository userRepository;
    
    private UserService userService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";

    @BeforeEach
    void setUp() {
        userService = new UserService(passwordEncoder, userRepository);
    }

    @Test
    void register_WithValidCredentials_ShouldSucceed() {
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(new User(TEST_EMAIL, ENCODED_PASSWORD));
        
        UserResponse response = userService.register(TEST_EMAIL, TEST_PASSWORD);
        assertEquals(TEST_EMAIL, response.email());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_WithExistingEmail_ShouldThrowException() {
        when(userRepository.existsByEmail(TEST_EMAIL)).thenReturn(true);
        
        InvalidUserDataException exception = assertThrows(InvalidUserDataException.class, () -> 
            userService.register(TEST_EMAIL, TEST_PASSWORD)
        );
        
        assertEquals("User with email " + TEST_EMAIL + " already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_WithEmptyEmail_ShouldThrowException() {
        InvalidUserDataException exception = assertThrows(InvalidUserDataException.class, () -> 
            userService.register("", TEST_PASSWORD)
        );
        
        assertEquals("Email and password cannot be empty", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_WithEmptyPassword_ShouldThrowException() {
        InvalidUserDataException exception = assertThrows(InvalidUserDataException.class, () -> 
            userService.register(TEST_EMAIL, "")
        );
        
        assertEquals("Email and password cannot be empty", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void userExist_WithValidCredentials_ShouldReturnTrue() {
        User mockUser = new User(TEST_EMAIL, ENCODED_PASSWORD);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(TEST_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        
        assertTrue(userService.userExist(TEST_EMAIL, TEST_PASSWORD));
    }

    @Test
    void userExist_WithInvalidCredentials_ShouldReturnFalse() {
        User mockUser = new User(TEST_EMAIL, ENCODED_PASSWORD);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(TEST_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);
        
        assertFalse(userService.userExist(TEST_EMAIL, TEST_PASSWORD));
    }
}
