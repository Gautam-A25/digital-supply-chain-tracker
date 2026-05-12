package com.digital.auth_service.controller;

import com.digital.auth_service.dto.RegisterRequestDto;
import com.digital.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/ping")
    public String ping() {
        return "auth-service is up";
    }

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequestDto request) {
        return authService.register(request);
    }
}