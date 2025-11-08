package com.smartexpense.controllers;



import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/example")
public class ExampleController {

    /**
     * Public endpoint accessible to anyone without authentication.
     */
    @GetMapping("/public")
    public String publicEndpoint() {
        return "no auth needed";
    }

    /**
     * Protected endpoint, only accessible by authenticated users with the ROLE_USER authority.
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    public String me(Authentication auth) {
        // Authentication object is automatically provided by Spring Security
        return "Hello " + auth.getName();
    }

    /**
     * Highly restricted endpoint, only accessible by authenticated users with the ROLE_ADMIN authority.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String adminOnly() {
        return "Only admins";
    }
}