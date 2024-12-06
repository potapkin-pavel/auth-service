package com.example.demo.user.role;

import org.springframework.stereotype.Service;

import com.example.demo.exception.InvalidUserDataException;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public RoleResponse createNewRole(RoleRequest roleRequest) {
        if(roleRequest.name().isEmpty()) {
            throw new InvalidUserDataException("Role name can not be empty.");
        }
        if(roleRepository.existsByName(roleRequest.name())) {
            throw new InvalidUserDataException("Role " + roleRequest.name() + " already exists.");
        }
        Role role = roleRepository.save(new Role(roleRequest.name().toUpperCase()));
        return RoleResponse.fromRole(role);
    }

    public void deleteRole(String roleId) {
        roleRepository.deleteById(Long.parseLong(roleId));
    }

    public List<RoleResponse> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(RoleResponse::fromRole).toList();
    }
}
