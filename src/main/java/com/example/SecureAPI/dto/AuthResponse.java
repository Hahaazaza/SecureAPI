package com.example.SecureAPI.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String role;

    public AuthResponse(String accessToken, String refreshToken, Long userId, String role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.role = role;
    }
}