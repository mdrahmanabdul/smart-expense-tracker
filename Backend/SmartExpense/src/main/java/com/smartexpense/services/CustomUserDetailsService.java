package com.smartexpense.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.smartexpense.repositories.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository repo;

    // Standard constructor injection replaces @RequiredArgsConstructor
    public CustomUserDetailsService(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find the user by username, or throw the standard exception if not found.
        return repo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}