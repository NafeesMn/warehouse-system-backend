package com.artiselite.warehouse.security;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.artiselite.warehouse.role.entity.Role;
import com.artiselite.warehouse.user.entity.User;
import com.artiselite.warehouse.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        customUserDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void inactiveUserCannotAuthenticate() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User inactiveUser = buildUser("inactive@artiselite.local", passwordEncoder.encode("Operator@123"), false, "OPERATOR");
        when(userRepository.findByEmailIgnoreCase("inactive@artiselite.local")).thenReturn(Optional.of(inactiveUser));

        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        assertThrows(
                DisabledException.class,
                () -> authenticationProvider.authenticate(
                        UsernamePasswordAuthenticationToken.unauthenticated(
                                "inactive@artiselite.local",
                                "Operator@123"
                        )
                )
        );
    }

    private User buildUser(String email, String passwordHash, boolean isActive, String roleName) {
        Role role = new Role();
        role.setRoleId(2L);
        role.setName(roleName);

        User user = new User();
        user.setUserId(30L);
        user.setFullName("Inactive Operator");
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setRole(role);
        user.setIsActive(isActive);
        user.setCreatedAt(LocalDateTime.of(2026, 3, 15, 8, 0));
        user.setUpdatedAt(LocalDateTime.of(2026, 3, 15, 8, 30));
        return user;
    }
}