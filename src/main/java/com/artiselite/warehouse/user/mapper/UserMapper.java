package com.artiselite.warehouse.user.mapper;

import com.artiselite.warehouse.user.dto.response.MyProfileResponse;
import com.artiselite.warehouse.user.dto.response.UserListItemResponse;
import com.artiselite.warehouse.user.dto.response.UserResponse;
import com.artiselite.warehouse.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getName(),
                Boolean.TRUE.equals(user.getIsActive()),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public UserListItemResponse toUserListItemResponse(User user) {
        return new UserListItemResponse(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getName(),
                Boolean.TRUE.equals(user.getIsActive()),
                user.getUpdatedAt()
        );
    }

    public MyProfileResponse toMyProfileResponse(User user) {
        return new MyProfileResponse(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getName(),
                Boolean.TRUE.equals(user.getIsActive()),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}