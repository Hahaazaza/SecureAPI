package com.example.SecureAPI.service;

import com.example.SecureAPI.model.RefreshToken;
import com.example.SecureAPI.repository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class JwtService {

    private final RefreshTokenRepository refreshTokenRepository;

    public String generateRefreshToken(Long userId) {
        String token = UUID.randomUUID().toString();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(token);
        refreshToken.setUserId(userId);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);

        return token;
    }

    public boolean validateRefreshToken(String token) {
        return refreshTokenRepository.findById(token)
                .map(rt -> !rt.isRevoked() && rt.getExpiryDate().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    public Long getUserIdFromRefreshToken(String token) {
        return refreshTokenRepository.findById(token)
                .map(RefreshToken::getUserId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
    }

    public void revokeAllTokensByUserId(Long userId) {
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUserId(userId);
        tokens.forEach(t -> t.setRevoked(true));
        refreshTokenRepository.saveAll(tokens);
    }
}