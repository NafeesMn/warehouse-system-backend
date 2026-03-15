package com.artiselite.warehouse.user.controller;

import com.artiselite.warehouse.common.api.ApiResponse;
import com.artiselite.warehouse.common.api.PagedResponse;
import com.artiselite.warehouse.user.dto.request.CreateUserRequest;
import com.artiselite.warehouse.user.dto.request.ResetUserPasswordRequest;
import com.artiselite.warehouse.user.dto.request.UpdateUserRequest;
import com.artiselite.warehouse.user.dto.request.UpdateUserStatusRequest;
import com.artiselite.warehouse.user.dto.response.UserListItemResponse;
import com.artiselite.warehouse.user.dto.response.UserResponse;
import com.artiselite.warehouse.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('MANAGER')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<PagedResponse<UserListItemResponse>> getUsers(
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fullName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        return ApiResponse.success(
                "Users loaded successfully.",
                userService.getUsers(roleName, isActive, page, size, sortBy, sortDirection)
        );
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable Long userId) {
        return ApiResponse.success("User loaded successfully.", userService.getUserById(userId));
    }

    @PostMapping
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ApiResponse.success("User created successfully.", userService.createUser(request));
    }

    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        return ApiResponse.success("User updated successfully.", userService.updateUser(userId, request));
    }

    @PatchMapping("/{userId}/status")
    public ApiResponse<UserResponse> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserStatusRequest request
    ) {
        return ApiResponse.success(
                "User status updated successfully.",
                userService.updateUserStatus(userId, request)
        );
    }

    @PatchMapping("/{userId}/reset-password")
    public ApiResponse<Void> resetUserPassword(
            @PathVariable Long userId,
            @Valid @RequestBody ResetUserPasswordRequest request
    ) {
        userService.resetUserPassword(userId, request);
        return ApiResponse.success("User password reset successfully.", null);
    }
}