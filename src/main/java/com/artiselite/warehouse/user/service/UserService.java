package com.artiselite.warehouse.user.service;

import com.artiselite.warehouse.user.dto.UserRequest;
import com.artiselite.warehouse.user.dto.UserResponse;
import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long userId);

    UserResponse createUser(UserRequest request);

    UserResponse updateUser(Long userId, UserRequest request);
}
