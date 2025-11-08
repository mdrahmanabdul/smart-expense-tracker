package com.smartexpense.security;

import com.smartexpense.dtos.AuthDTO.LoginRequest;
import com.smartexpense.dtos.AuthDTO.RegisterRequest;
import com.smartexpense.entities.Role;
import com.smartexpense.entities.UserEntity;
import com.smartexpense.repositories.UserRepository;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    // Standard constructor injection replaces @RequiredArgsConstructor
    public AuthService(UserRepository repo, PasswordEncoder encoder, JwtService jwtService) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public void register(RegisterRequest req) {
        if (repo.existsByUsername(req.username())) {
            throw new IllegalArgumentException("Username already taken");
        }

        UserEntity user = new UserEntity();
        user.setUsername(req.username());
        user.setPassword(encoder.encode(req.password())); // BCrypt using injected encoder
        user.setEnabled(true);

        // Default role setup (ensure Role entity and relationship are managed correctly)
        Role role = new Role();
        role.setName("ROLE_USER");
        user.getRoles().add(role);
        
        repo.save(user);
    }

    public String login(LoginRequest req) {
        UserEntity user = repo.findByUsername(req.username())
            .orElseThrow(() -> new BadCredentialsException("Bad credentials"));
        
        // Use the injected encoder for password verification
        if (!user.isEnabled() || !encoder.matches(req.password(), user.getPassword())) {
            throw new BadCredentialsException("Bad credentials");
        }

        return jwtService.generateToken(user);
    }
}