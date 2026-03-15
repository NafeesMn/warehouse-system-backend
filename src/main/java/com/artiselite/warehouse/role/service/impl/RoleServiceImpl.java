package com.artiselite.warehouse.role.service.impl;

import com.artiselite.warehouse.exception.ResourceNotFoundException;
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
        return roleRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + name));
    }
}
