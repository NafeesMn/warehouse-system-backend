package com.artiselite.warehouse.role.service.impl;

import com.artiselite.warehouse.exception.BadRequestException;
import com.artiselite.warehouse.role.dto.RoleResponse;
import com.artiselite.warehouse.role.entity.Role;
import com.artiselite.warehouse.role.repository.RoleRepository;
import com.artiselite.warehouse.role.service.RoleService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> new RoleResponse(role.getRoleId(), role.getName()))
                .toList();
    }

    @Override
    public Role getRequiredRole(String name) {
        String normalizedRoleName = normalizeRoleName(name);
        return roleRepository.findByNameIgnoreCase(normalizedRoleName)
                .orElseThrow(() -> new BadRequestException("Invalid role: " + name + ". Allowed values: MANAGER, OPERATOR."));
    }

    private String normalizeRoleName(String name) {
        if (name == null) {
            throw new BadRequestException("roleName must be MANAGER or OPERATOR.");
        }

        String normalized = name.trim().toUpperCase();
        if (!"MANAGER".equals(normalized) && !"OPERATOR".equals(normalized)) {
            throw new BadRequestException("roleName must be MANAGER or OPERATOR.");
        }
        return normalized;
    }
}