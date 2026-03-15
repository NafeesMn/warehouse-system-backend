package com.artiselite.warehouse.user.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.artiselite.warehouse.exception.BadRequestException;
import com.artiselite.warehouse.exception.DuplicateResourceException;
import com.artiselite.warehouse.role.entity.Role;
import com.artiselite.warehouse.role.service.RoleService;
import com.artiselite.warehouse.user.dto.request.ChangeOwnPasswordRequest;
import com.artiselite.warehouse.user.dto.request.CreateUserRequest;
import com.artiselite.warehouse.user.dto.request.ResetUserPasswordRequest;
import com.artiselite.warehouse.user.dto.response.UserResponse;
import com.artiselite.warehouse.user.entity.User;
import com.artiselite.warehouse.user.mapper.UserMapper;
import com.artiselite.warehouse.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, roleService, passwordEncoder, new UserMapper());
    }

    @Test
    void createUserShouldSucceed() {
        CreateUserRequest request = new CreateUserRequest(
                "Alice Manager",
                "Alice.Manager@Artiselite.local",
                "Manager@123",
                "MANAGER",
                true
        );
        Role managerRole = buildRole(1L, "MANAGER");

        when(userRepository.existsByEmailIgnoreCase("alice.manager@artiselite.local")).thenReturn(false);
        when(roleService.getRequiredRole("MANAGER")).thenReturn(managerRole);
        when(passwordEncoder.encode("Manager@123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(10L);
            user.setCreatedAt(LocalDateTime.of(2026, 3, 15, 9, 0));
            user.setUpdatedAt(LocalDateTime.of(2026, 3, 15, 9, 0));
            return user;
        });

        UserResponse response = userService.createUser(request);

        assertThat(response.userId()).isEqualTo(10L);
        assertThat(response.email()).isEqualTo("alice.manager@artiselite.local");
        assertThat(response.roleName()).isEqualTo("MANAGER");
        assertThat(response.isActive()).isTrue();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUserShouldRejectDuplicateEmail() {
        CreateUserRequest request = new CreateUserRequest(
                "Alice Manager",
                "alice.manager@artiselite.local",
                "Manager@123",
                "MANAGER",
                true
        );

        when(userRepository.existsByEmailIgnoreCase("alice.manager@artiselite.local")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(request));
    }

    @Test
    void resetUserPasswordShouldEncodeNewPassword() {
        User user = buildUser(15L, "manager@artiselite.local", "MANAGER");
        when(userRepository.findById(15L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("NewManager@123")).thenReturn("encoded-new-password");

        userService.resetUserPassword(15L, new ResetUserPasswordRequest("NewManager@123"));

        assertThat(user.getPasswordHash()).isEqualTo("encoded-new-password");
        verify(userRepository).save(user);
    }

    @Test
    void changeOwnPasswordShouldRejectIncorrectCurrentPassword() {
        User user = buildUser(20L, "operator@artiselite.local", "OPERATOR");
        user.setPasswordHash("stored-hash");

        when(userRepository.findByEmailIgnoreCase("operator@artiselite.local")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword", "stored-hash")).thenReturn(false);

        assertThrows(
                BadRequestException.class,
                () -> userService.changeOwnPassword(
                        "operator@artiselite.local",
                        new ChangeOwnPasswordRequest("WrongPassword", "Operator@456")
                )
        );
    }

    private Role buildRole(Long roleId, String name) {
        Role role = new Role();
        role.setRoleId(roleId);
        role.setName(name);
        return role;
    }

    private User buildUser(Long userId, String email, String roleName) {
        User user = new User();
        user.setUserId(userId);
        user.setFullName("Warehouse User");
        user.setEmail(email);
        user.setIsActive(true);
        user.setRole(buildRole(1L, roleName));
        user.setCreatedAt(LocalDateTime.of(2026, 3, 15, 8, 0));
        user.setUpdatedAt(LocalDateTime.of(2026, 3, 15, 8, 30));
        return user;
    }
}