package com.smartinventory.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String SECRET_KEY =
            "SmartInventoryManagementJWTSecretKey2026";

    private static final long EXPIRATION_TIME =
            1000L * 60 * 60 * 24;

    private final Key key =
            Keys.hmacShaKeyFor(
                    SECRET_KEY.getBytes(StandardCharsets.UTF_8)
            );

    // ==========================
    // Generate JWT Token
    // ==========================
    public String generateToken(
            String username,
            String role) {

        Map<String, Object> claims =
                new HashMap<>();

        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + EXPIRATION_TIME
                        )
                )
                .signWith(
                        key,
                        SignatureAlgorithm.HS256
                )
                .compact();
    }

    // ==========================
    // Extract Claims
    // ==========================
    public Claims extractClaims(
            String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ==========================
    // Extract Username
    // ==========================
    public String extractUsername(
            String token) {

        return extractClaims(token)
                .getSubject();
    }

    // ==========================
    // Extract Role
    // ==========================
    public String extractRole(
            String token) {

        return extractClaims(token)
                .get("role", String.class);
    }

    // ==========================
    // Check Token Expiration
    // ==========================
    public boolean isTokenExpired(
            String token) {

        return extractClaims(token)
                .getExpiration()
                .before(new Date());
    }

    // ==========================
    // Validate Token
    // ==========================
    public boolean validateToken(
            String token,
            String username) {

        return username != null
                && username.equals(
                extractUsername(token)
        )
                && !isTokenExpired(token);
    }
}