package com.financemanager.service;

import com.financemanager.dto.AuthResponse;
import com.financemanager.dto.LoginRequest;
import com.financemanager.entity.User;
import com.financemanager.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceExtraTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @InjectMocks private AuthenticationService authenticationService;

    @Mock private HttpServletRequest httpRequest;
    @Mock private HttpSession httpSession;

    private LoginRequest loginRequest;

    @BeforeEach
    void setup() {
        loginRequest = LoginRequest.builder()
                .username("test@example.com")
                .password("secret")
                .build();
        SecurityContextHolder.clearContext();
    }

    @Test
    void login_success_setsSecurityContextAndSession() {
        Authentication auth = new UsernamePasswordAuthenticationToken("test@example.com", "secret");
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(auth);
        when(httpRequest.getSession(true)).thenReturn(httpSession);

        AuthResponse resp = authenticationService.login(loginRequest, httpRequest);

        assertNotNull(resp);
        assertEquals("Login successful", resp.getMessage());

        SecurityContext ctx = SecurityContextHolder.getContext();
        assertNotNull(ctx.getAuthentication());
        assertEquals("test@example.com", ctx.getAuthentication().getName());

        verify(httpSession).setAttribute(eq(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY), any(SecurityContext.class));
    }

    @Test
    void logout_invalidatesSession_andClearsContext() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken("user", "x"));
        SecurityContextHolder.setContext(context);

        when(httpRequest.getSession(false)).thenReturn(httpSession);

        authenticationService.logout(httpRequest);

        verify(httpSession, times(1)).invalidate();
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void getCurrentUser_returnsUserFromRepository() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken("test@example.com", "x"));
        SecurityContextHolder.setContext(context);

        User user = User.builder().id(1L).username("test@example.com").build();
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(user));

        User result = authenticationService.getCurrentUser();
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getUsername());
    }

    @Test
    void getCurrentUser_notFound_throws() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken("ghost@example.com", "x"));
        SecurityContextHolder.setContext(context);

        when(userRepository.findByUsername("ghost@example.com")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> authenticationService.getCurrentUser());
    }
}
