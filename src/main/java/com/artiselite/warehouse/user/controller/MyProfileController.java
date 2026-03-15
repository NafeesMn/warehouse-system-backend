package com.artiselite.warehouse.user.controller;

import com.artiselite.warehouse.common.api.ApiResponse;
import com.artiselite.warehouse.user.dto.request.ChangeOwnPasswordRequest;
import com.artiselite.warehouse.user.dto.response.MyProfileResponse;
import com.artiselite.warehouse.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@PreAuthorize("isAuthenticated()")
public class MyProfileController {

    private final UserService userService;

    public MyProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<MyProfileResponse> getMyProfile(Authentication authentication) {
        return ApiResponse.success(
                "Profile loaded successfully.",
                userService.getMyProfile(authentication.getName())
        );
    }

    @PatchMapping("/change-password")
    public ApiResponse<Void> changeOwnPassword(
            @Valid @RequestBody ChangeOwnPasswordRequest request,
            Authentication authentication
    ) {
        userService.changeOwnPassword(authentication.getName(), request);
        return ApiResponse.success("Password changed successfully.", null);
    }
}