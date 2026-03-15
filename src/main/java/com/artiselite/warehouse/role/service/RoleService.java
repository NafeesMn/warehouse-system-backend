package com.artiselite.warehouse.role.service;

import com.artiselite.warehouse.role.dto.RoleResponse;
import com.artiselite.warehouse.role.entity.Role;
import java.util.List;

public interface RoleService {

    List<RoleResponse> getAllRoles();

    Role getRequiredRole(String name);
}
