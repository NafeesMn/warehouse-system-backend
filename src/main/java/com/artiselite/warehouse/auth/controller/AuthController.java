package com.artiselite.warehouse.auth.controller;

import com.artiselite.warehouse.auth.dto.AuthResponse;
import com.artiselite.warehouse.auth.dto.CurrentUserResponse;
import com.artiselite.warehouse.auth.dto.LoginRequest;
import com.artiselite.warehouse.auth.service.AuthService;
import com.artiselite.warehouse.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success("Login successful.", authService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUserResponse> currentUser(Authentication authentication) {
        return ApiResponse.success(
                "Current user loaded successfully.",
                authService.getCurrentUser(authentication.getName())
        );
    }
}
