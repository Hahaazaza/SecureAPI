package com.example.SecureAPI.repository;

import com.example.SecureAPI.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByUserId(Long userId);
    Optional<RefreshToken> findById(String id);
}