package com.artiselite.warehouse.user.service;

import com.artiselite.warehouse.common.api.PagedResponse;
import com.artiselite.warehouse.user.dto.request.ChangeOwnPasswordRequest;
import com.artiselite.warehouse.user.dto.request.CreateUserRequest;
import com.artiselite.warehouse.user.dto.request.ResetUserPasswordRequest;
import com.artiselite.warehouse.user.dto.request.UpdateUserRequest;
import com.artiselite.warehouse.user.dto.request.UpdateUserStatusRequest;
import com.artiselite.warehouse.user.dto.response.MyProfileResponse;
import com.artiselite.warehouse.user.dto.response.UserListItemResponse;
import com.artiselite.warehouse.user.dto.response.UserResponse;

public interface UserService {

    PagedResponse<UserListItemResponse> getUsers(
            String roleName,
            Boolean isActive,
            int page,
            int size,
            String sortBy,
            String sortDirection
    );

    UserResponse getUserById(Long userId);

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(Long userId, UpdateUserRequest request);

    UserResponse updateUserStatus(Long userId, UpdateUserStatusRequest request);

    void resetUserPassword(Long userId, ResetUserPasswordRequest request);

    MyProfileResponse getMyProfile(String email);

    void changeOwnPassword(String email, ChangeOwnPasswordRequest request);
}