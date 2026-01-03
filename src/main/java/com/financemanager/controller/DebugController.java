package com.financemanager.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @GetMapping("/session")
    public ResponseEntity<Map<String, Object>> session(HttpServletRequest request) {
        Map<String, Object> out = new HashMap<>();

        HttpSession session = request.getSession(false);
        if (session != null) {
            out.put("sessionId", session.getId());
            Enumeration<String> names = session.getAttributeNames();
            Map<String, Object> attrs = new HashMap<>();
            while (names.hasMoreElements()) {
                String n = names.nextElement();
                attrs.put(n, session.getAttribute(n));
            }
            out.put("sessionAttributes", attrs);
        } else {
            out.put("sessionId", null);
            out.put("sessionAttributes", Collections.emptyMap());
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !(auth instanceof AnonymousAuthenticationToken) && auth.isAuthenticated()) {
            out.put("principal", auth.getName());
            List<String> authorities = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            out.put("authorities", authorities);
        } else {
            out.put("principal", null);
            out.put("authorities", Collections.emptyList());
        }

        return ResponseEntity.ok(out);
    }
}
