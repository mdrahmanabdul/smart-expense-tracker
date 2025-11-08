package com.smartexpense.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

// This class no longer needs @RequiredArgsConstructor as it relies on @Value for field injection.
@Service
public class JwtService {
    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiryMinutes}")
    private long expiryMinutes;

    @Value("${app.jwt.issuer}")
    private String issuer;

    /**
     * Generates the HMAC-SHA key from the base64-encoded secret string.
     */
    private Key key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a new JWT for a given user.
     *
     * @param user The UserDetails object containing user information.
     * @return The generated JWT string.
     */
    public String generateToken(UserDetails user) {
        Instant now = Instant.now();
        return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("roles", user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList())
            .setIssuer(issuer)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(expiryMinutes, ChronoUnit.MINUTES)))
            .signWith(key(), SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * Extracts the username (subject) from a JWT.
     *
     * @param token The JWT string.
     * @return The username.
     */
    public String extractUsername(String token) {
        return parseAllClaims(token).getSubject();
    }

    /**
     * Validates if the token is valid for the given user and has not expired.
     *
     * @param token The JWT string.
     * @param user The UserDetails object to compare against.
     * @return True if the token is valid, false otherwise.
     */
    public boolean isTokenValid(String token, UserDetails user) {
        Claims claims = parseAllClaims(token);
        boolean notExpired = claims.getExpiration().after(new Date());
        return notExpired && user.getUsername().equals(claims.getSubject());
    }

    /**
     * Parses and validates the JWT, returning all claims.
     *
     * @param token The JWT string.
     * @return The Claims body.
     */
    private Claims parseAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}