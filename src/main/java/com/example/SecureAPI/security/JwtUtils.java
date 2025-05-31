package com.example.SecureAPI.security;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String secretString;

    @Value("${app.jwt.expiration}")
    private long expiration;

    private Key SIGNING_KEY;

    @PostConstruct
    public void init() {
        byte[] secretBytes = Base64.getDecoder().decode(secretString);
        this.SIGNING_KEY = new SecretKeySpec(secretBytes, SignatureAlgorithm.HS512.getJcaName());
    }

    public String generateToken(Long userId, String role) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setNotBefore(new Date(System.currentTimeMillis() + 1000))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SIGNING_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SIGNING_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SIGNING_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SIGNING_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}