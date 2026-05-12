package com.digital.auth_service.service;

import com.digital.auth_service.dto.RegisterRequestDto;
import com.digital.auth_service.entity.User;
import com.digital.auth_service.enums.AuthProvider;
import com.digital.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String register(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email already registered";
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .roles(Collections.singleton(request.getRole()))
                .provider(AuthProvider.LOCAL)
                .providerId(null)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        return "User registered successfully";
    }
}