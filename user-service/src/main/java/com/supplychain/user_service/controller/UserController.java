package com.supplychain.user_service.controller;

import com.supplychain.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    // User CRUD APIs by Anirudh

    private final UserService userService;

    @GetMapping("/test")
    public String testApi() {
        return "User Service Working Successfully";
    }
}