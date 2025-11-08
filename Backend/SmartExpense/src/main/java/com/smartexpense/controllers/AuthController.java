	package com.smartexpense.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.smartexpense.dtos.AuthDTO.AuthResponse;
import com.smartexpense.dtos.AuthDTO.LoginRequest;
import com.smartexpense.dtos.AuthDTO.RegisterRequest;
import com.smartexpense.entities.UserEntity;
import com.smartexpense.repositories.UserRepository;
import com.smartexpense.security.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    // Standard constructor injection replaces @RequiredArgsConstructor
    public AuthController(
        AuthenticationManager authManager, 
        JwtService jwtService, 
        UserRepository repo, 
        PasswordEncoder encoder) 
    {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.repo = repo;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Validated RegisterRequest req) {
        if (repo.existsByUsername(req.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }
        
        UserEntity user = new UserEntity();
        user.setUsername(req.username());
        user.setPassword(encoder.encode(req.password()));
        user.setEnabled(true);
        
        // TODO: attach ROLE_USER from RoleRepo (recommended)
        
        repo.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Validated LoginRequest req) {
        // 1. Authenticate the user credentials using the AuthenticationManager
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        
        // 2. Generate the JWT token upon successful authentication
        String token = jwtService.generateToken((UserDetails) auth.getPrincipal());
        
        // 3. Return the token in the response body
        return ResponseEntity.ok(new AuthResponse(token));
    }
}