package com.artiselite.warehouse.auth.service.impl;

import com.artiselite.warehouse.auth.dto.AuthResponse;
import com.artiselite.warehouse.auth.dto.CurrentUserResponse;
import com.artiselite.warehouse.auth.dto.LoginRequest;
import com.artiselite.warehouse.auth.service.AuthService;
import com.artiselite.warehouse.exception.ResourceNotFoundException;
import com.artiselite.warehouse.security.JwtService;
import com.artiselite.warehouse.user.entity.User;
import com.artiselite.warehouse.user.repository.UserRepository;
import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        String token = jwtService.generateToken(user.getEmail(), List.of(user.getRole().getName()));
        return new AuthResponse(
                token,
                "Bearer",
                jwtService.calculateExpiryInstant(),
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getName()
        );
    }

    @Override
    public CurrentUserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        return new CurrentUserResponse(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getName(),
                user.getIsActive()
        );
    }
}
