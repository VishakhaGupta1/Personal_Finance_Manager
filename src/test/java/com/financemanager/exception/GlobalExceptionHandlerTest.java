package com.financemanager.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setup() {
        handler = new GlobalExceptionHandler();
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn("/test");
    }

    @Test
    void duplicateResource_returns409() {
        ResponseEntity<ErrorResponse> resp = handler.handleDuplicateResource(new DuplicateResourceException("dup"), request);
        assertEquals(409, resp.getStatusCode().value());
        assertTrue(resp.getBody().getMessage().contains("dup"));
    }

    @Test
    void forbidden_returns403() {
        ResponseEntity<ErrorResponse> resp = handler.handleForbidden(new ForbiddenException("no"), request);
        assertEquals(403, resp.getStatusCode().value());
    }

    @Test
    void authFailure_returns401() {
        ResponseEntity<ErrorResponse> resp = handler.handleAuthenticationException(new BadCredentialsException("bad"), request);
        assertEquals(401, resp.getStatusCode().value());
    }

    @Test
    void accessDenied_returns403() {
        ResponseEntity<ErrorResponse> resp = handler.handleAccessDeniedException(new AccessDeniedException("deny"), request);
        assertEquals(403, resp.getStatusCode().value());
    }

    @Test
    void illegalArg_returns400() {
        ResponseEntity<ErrorResponse> resp = handler.handleIllegalArgumentException(new IllegalArgumentException("bad"), request);
        assertEquals(400, resp.getStatusCode().value());
    }

    @Test
    void generic_returns500() {
        ResponseEntity<ErrorResponse> resp = handler.handleGlobalException(new RuntimeException("oops"), request);
        assertEquals(500, resp.getStatusCode().value());
    }
}
