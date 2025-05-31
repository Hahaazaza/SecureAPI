package com.example.SecureAPI.security;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {
    private final String SECRET_STRING = "VGhpcyBpcyBhIHNlY3VyZSByYW5kb20gc2VjcmV0IGtleSBmb3IgSFNGMTI4IHdoaWNoIGlzIHN1ZmZpY2llbnRseSBsb25nIGVuY291Z2ggdG8gdXNlIHdpdGggSFN4IGFsZ29yaXRobXM=";
    private final byte[] secretBytes = Base64.getDecoder().decode(SECRET_STRING);
    private final Key SIGNING_KEY = new SecretKeySpec(secretBytes, SignatureAlgorithm.HS512.getJcaName());
    private final long EXPIRATION = 86400000; // 24 часа

    public String generateToken(Long userId, String role) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", role)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
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
            Jwts.parserBuilder().setSigningKey(SIGNING_KEY).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}