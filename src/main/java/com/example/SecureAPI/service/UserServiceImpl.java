package com.example.SecureAPI.service;

import com.example.SecureAPI.dto.AuthResponse;
import com.example.SecureAPI.dto.RegisterRequest;
import com.example.SecureAPI.exception.InvalidCredentialsException;
import com.example.SecureAPI.exception.UserAlreadyExistsException;
import com.example.SecureAPI.model.User;
import com.example.SecureAPI.repository.UserRepository;
import com.example.SecureAPI.security.JwtUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public void register(RegisterRequest request) {
        Objects.requireNonNull(request, "Register request cannot be null");

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }

        logger.info("Registering user: {}", request.getEmail());

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setRole(normalizeRole(request.getRole()));

        userRepository.save(user);
    }

    @Override
    public AuthResponse login(String email, String password) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String token = jwtUtils.generateToken(user.getId(), user.getRole());

        return new AuthResponse(token, user.getRole(), user.getId());
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "USER";
        }
        return role.toUpperCase();
    }
}