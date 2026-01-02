package com.financemanager.service;

import com.financemanager.dto.AuthResponse;
import com.financemanager.dto.LoginRequest;
import com.financemanager.dto.RegisterRequest;
import com.financemanager.entity.User;
import com.financemanager.exception.DuplicateResourceException;
import com.financemanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for user authentication and authorization.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Register a new user.
     *
     * @param request Registration request containing user details
     * @return AuthResponse with user ID
     * @throws DuplicateResourceException if username already exists
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + request.getUsername());
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .build();

        User savedUser = userRepository.save(user);

        return AuthResponse.builder()
                .message("User registered successfully")
                .userId(savedUser.getId())
                .build();
    }

    /**
     * Login a user.
     *
     * @param request Login request containing username and password
     * @return AuthResponse with success message
     */
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return AuthResponse.builder()
                .message("Login successful")
                .build();
    }

    /**
     * Logout the current user.
     *
     * @return Success message
     */
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Get the currently authenticated user.
     *
     * @return Current authenticated User
     */
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
