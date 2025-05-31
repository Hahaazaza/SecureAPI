package com.example.SecureAPI.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String name;
    private String role; // ADMIN, EMPLOYEE, CLIENT
}