package com.example.SecureAPI.repository;

import com.example.SecureAPI.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    List<RefreshToken> findAllByUserId(Long userId);
}