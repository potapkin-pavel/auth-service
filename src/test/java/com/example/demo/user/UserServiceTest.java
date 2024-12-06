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
import com.example.demo.user.role.RoleRepository;
import com.example.demo.user.role.Role;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    private UserService userService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";

    @BeforeEach
    void setUp() {
        userService = new UserService(passwordEncoder, userRepository, roleRepository);
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
        
        assertEquals("Email and password can not be empty.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_WithEmptyPassword_ShouldThrowException() {
        assertThrows(InvalidUserDataException.class, () -> userService.register(TEST_EMAIL, ""));
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

    @Test
    void addRoleToUser_WithValidRole_ShouldSucceed() {
        Long userId = 1L;
        String roleName = "ADMIN";
        User user = new User(TEST_EMAIL, ENCODED_PASSWORD);
        Role role = new Role();
        role.setName(roleName);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.addRoleToUser(userId, roleName);
        
        assertNotNull(result);
        assertTrue(result.getRoles().contains(role));
        verify(userRepository).save(user);
    }

    @Test
    void addRoleToUser_WithNonExistentRole_ShouldThrowException() {
        Long userId = 1L;
        String roleName = "NON_EXISTENT";
        User user = new User(TEST_EMAIL, ENCODED_PASSWORD);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(roleName)).thenReturn(Optional.empty());

        assertThrows(jakarta.persistence.EntityNotFoundException.class, 
            () -> userService.addRoleToUser(userId, roleName));
        
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void removeRoleFromUser_WithValidRole_ShouldSucceed() {
        Long userId = 1L;
        String roleName = "ADMIN";
        User user = new User(TEST_EMAIL, ENCODED_PASSWORD);
        Role role = new Role();
        role.setName(roleName);
        user.addRole(role);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.removeRoleFromUser(userId, roleName);
        
        assertNotNull(result);
        assertFalse(result.getRoles().contains(role));
        verify(userRepository).save(user);
    }
}
