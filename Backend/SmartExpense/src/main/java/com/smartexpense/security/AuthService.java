package com.smartexpense.security;

import com.smartexpense.dtos.AuthDTO.AuthResponse;
import com.smartexpense.dtos.AuthDTO.LoginRequest;
import com.smartexpense.dtos.AuthDTO.RegisterRequest;
import com.smartexpense.entities.Role;
import com.smartexpense.entities.UserEntity;
import com.smartexpense.repositories.RoleRepository;
import com.smartexpense.repositories.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepo,
                       RoleRepository roleRepo,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authManager,
                       JwtService jwtService) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public void register(RegisterRequest req) {
        if (userRepo.existsByUsername(req.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }
        if (userRepo.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        UserEntity user = new UserEntity();
        user.setUsername(req.username());
        user.setEmail(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setEnabled(true);

        // ✅ Fetch role from DB (avoid duplicates)
        Role userRole = roleRepo.findByName("ROLE_USER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Default role not found"));
        user.getRoles().add(userRole);

        userRepo.save(user);
    }

    public AuthResponse login(LoginRequest req) {
        // 1️⃣ Try to find user by username or email
        UserEntity user = userRepo.findByUsername(req.username())
                .or(() -> userRepo.findByEmail(req.username()))
                .orElseThrow(() -> new BadCredentialsException("Invalid username/email or password"));

        // 2️⃣ Validate password
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username/email or password");
        }
        
        // 3️⃣ Generate JWT
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

}
