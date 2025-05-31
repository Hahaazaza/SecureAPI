package com.example.SecureAPI.service;

import com.example.SecureAPI.model.RefreshToken;
import com.example.SecureAPI.model.User;
import com.example.SecureAPI.repository.RefreshTokenRepository;
import com.example.SecureAPI.security.JwtUtils;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class JwtService {

    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtService(JwtUtils jwtUtils, RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtils = jwtUtils;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String generateAccessToken(User user) {
        return jwtUtils.generateToken(user.getId(), user.getRole().name());
    }

    public String generateRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(token);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    public boolean validateRefreshToken(String token) {
        return refreshTokenRepository.findById(token)
                .map(t -> !t.isRevoked() && t.getExpiryDate().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    public Long getUserIdFromRefreshToken(String token) {
        return refreshTokenRepository.findById(token)
                .map(rt -> rt.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
    }

    public void revokeAllTokensByUserId(Long userId) {
        refreshTokenRepository.findByUserId(userId).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }
}