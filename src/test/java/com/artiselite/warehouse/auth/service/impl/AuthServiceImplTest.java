package com.artiselite.warehouse.auth.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.artiselite.warehouse.auth.dto.AuthResponse;
import com.artiselite.warehouse.auth.dto.LoginRequest;
import com.artiselite.warehouse.role.entity.Role;
import com.artiselite.warehouse.security.JwtService;
import com.artiselite.warehouse.user.entity.User;
import com.artiselite.warehouse.user.repository.UserRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(authenticationManager, jwtService, userRepository);
    }

    @Test
    void loginShouldReturnJwtResponse() {
        LoginRequest request = new LoginRequest("manager@artiselite.local", "Manager@123");
        User user = buildUser();
        Instant expiresAt = Instant.parse("2026-03-15T12:00:00Z");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        when(userRepository.findByEmailIgnoreCase("manager@artiselite.local")).thenReturn(Optional.of(user));
        when(jwtService.generateToken("manager@artiselite.local", java.util.List.of("MANAGER"))).thenReturn("jwt-token");
        when(jwtService.calculateExpiryInstant()).thenReturn(expiresAt);

        AuthResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("jwt-token");
        assertThat(response.email()).isEqualTo("manager@artiselite.local");
        assertThat(response.role()).isEqualTo("MANAGER");
        assertThat(response.expiresAt()).isEqualTo(expiresAt);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    private User buildUser() {
        Role role = new Role();
        role.setRoleId(1L);
        role.setName("MANAGER");

        User user = new User();
        user.setUserId(1L);
        user.setFullName("System Manager");
        user.setEmail("manager@artiselite.local");
        user.setRole(role);
        return user;
    }
}