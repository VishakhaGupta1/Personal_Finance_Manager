package com.financemanager.controller;

import com.financemanager.dto.AuthResponse;
import com.financemanager.dto.LoginRequest;
import com.financemanager.dto.MessageResponse;
import com.financemanager.dto.RegisterRequest;
import com.financemanager.service.AuthenticationService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user authentication endpoints.
 */
@RestController
@RequestMapping({"/api/auth", "/auth"})
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    /**
     * Register a new user.
     *
     * @param request Registration request
     * @return AuthResponse with user ID
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authenticationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login a user.
     *
     * @param request Login request
     * @return AuthResponse with success message
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        AuthResponse response = authenticationService.login(request, httpRequest);
        var session = httpRequest.getSession(true);
        boolean secure = httpRequest.isSecure();
        String cookie = String.format("JSESSIONID=%s; Path=/; HttpOnly; SameSite=Lax%s", session.getId(), secure ? "; Secure" : "");
        httpResponse.addHeader("Set-Cookie", cookie);
        return ResponseEntity.ok(response);
    }

    /**
     * Logout the current user.
     *
     * @return MessageResponse with success message
     */
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        authenticationService.logout(httpRequest);
        boolean secure = httpRequest.isSecure();
        String cookie = String.format("JSESSIONID=; Path=/; HttpOnly; SameSite=Lax%s; Max-Age=0", secure ? "; Secure" : "");
        httpResponse.addHeader("Set-Cookie", cookie);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Logout successful")
                .build());
    }
}
