package com.artiselite.warehouse.auth.service;

import com.artiselite.warehouse.auth.dto.AuthResponse;
import com.artiselite.warehouse.auth.dto.CurrentUserResponse;
import com.artiselite.warehouse.auth.dto.LoginRequest;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    CurrentUserResponse getCurrentUser(String email);
}
