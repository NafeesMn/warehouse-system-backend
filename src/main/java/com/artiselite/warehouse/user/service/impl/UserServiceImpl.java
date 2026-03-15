package com.artiselite.warehouse.user.service.impl;

import com.artiselite.warehouse.common.api.PagedResponse;
import com.artiselite.warehouse.exception.BadRequestException;
import com.artiselite.warehouse.exception.DuplicateResourceException;
import com.artiselite.warehouse.exception.ResourceNotFoundException;
import com.artiselite.warehouse.role.entity.Role;
import com.artiselite.warehouse.role.service.RoleService;
import com.artiselite.warehouse.user.dto.request.ChangeOwnPasswordRequest;
import com.artiselite.warehouse.user.dto.request.CreateUserRequest;
import com.artiselite.warehouse.user.dto.request.ResetUserPasswordRequest;
import com.artiselite.warehouse.user.dto.request.UpdateUserRequest;
import com.artiselite.warehouse.user.dto.request.UpdateUserStatusRequest;
import com.artiselite.warehouse.user.dto.response.MyProfileResponse;
import com.artiselite.warehouse.user.dto.response.UserListItemResponse;
import com.artiselite.warehouse.user.dto.response.UserResponse;
import com.artiselite.warehouse.user.entity.User;
import com.artiselite.warehouse.user.mapper.UserMapper;
import com.artiselite.warehouse.user.repository.UserRepository;
import com.artiselite.warehouse.user.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(
            UserRepository userRepository,
            RoleService roleService,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserListItemResponse> getUsers(
            String roleName,
            Boolean isActive,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        String normalizedRoleName = StringUtils.hasText(roleName)
                ? roleService.getRequiredRole(roleName).getName()
                : null;

        return PagedResponse.from(userRepository.findAllByFilters(
                normalizedRoleName,
                isActive,
                PageRequest.of(page, size, Sort.by(resolveSortDirection(sortDirection), resolveSortBy(sortBy)))
        ).map(userMapper::toUserListItemResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        return userMapper.toUserResponse(getRequiredUser(userId));
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new DuplicateResourceException("Email is already in use.");
        }

        Role role = roleService.getRequiredRole(request.roleName());
        User user = new User();
        user.setFullName(normalizeFullName(request.fullName()));
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(role);
        user.setIsActive(request.isActive() == null || request.isActive());

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = getRequiredUser(userId);

        if (!hasUserUpdateFields(request)) {
            throw new BadRequestException("At least one updatable field must be provided.");
        }

        if (StringUtils.hasText(request.fullName())) {
            user.setFullName(normalizeFullName(request.fullName()));
        }

        if (StringUtils.hasText(request.email())) {
            String normalizedEmail = normalizeEmail(request.email());
            if (!user.getEmail().equalsIgnoreCase(normalizedEmail)
                    && userRepository.existsByEmailIgnoreCaseAndUserIdNot(normalizedEmail, userId)) {
                throw new DuplicateResourceException("Email is already in use.");
            }
            user.setEmail(normalizedEmail);
        }

        if (StringUtils.hasText(request.roleName())) {
            user.setRole(roleService.getRequiredRole(request.roleName()));
        }

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(Long userId, UpdateUserStatusRequest request) {
        User user = getRequiredUser(userId);
        user.setIsActive(request.isActive());
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void resetUserPassword(Long userId, ResetUserPasswordRequest request) {
        User user = getRequiredUser(userId);
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public MyProfileResponse getMyProfile(String email) {
        return userMapper.toMyProfileResponse(getRequiredUserByEmail(email));
    }

    @Override
    @Transactional
    public void changeOwnPassword(String email, ChangeOwnPasswordRequest request) {
        User user = getRequiredUserByEmail(email);

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect.");
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
            throw new BadRequestException("New password must be different from the current password.");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    private User getRequiredUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    private User getRequiredUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private String normalizeFullName(String fullName) {
        return fullName.trim();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private boolean hasUserUpdateFields(UpdateUserRequest request) {
        return StringUtils.hasText(request.fullName())
                || StringUtils.hasText(request.email())
                || StringUtils.hasText(request.roleName());
    }

    private Sort.Direction resolveSortDirection(String sortDirection) {
        return "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
    }

    private String resolveSortBy(String sortBy) {
        if ("email".equalsIgnoreCase(sortBy)) {
            return "email";
        }
        if ("createdAt".equalsIgnoreCase(sortBy)) {
            return "createdAt";
        }
        if ("updatedAt".equalsIgnoreCase(sortBy)) {
            return "updatedAt";
        }
        return "fullName";
    }
}