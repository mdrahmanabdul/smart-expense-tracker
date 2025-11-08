package com.smartexpense.controllers;

import com.smartexpense.dtos.AuthDTO.AuthResponse;
import com.smartexpense.dtos.AuthDTO.LoginRequest;
import com.smartexpense.dtos.AuthDTO.RegisterRequest;
import com.smartexpense.security.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Validated RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Validated LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }
}
