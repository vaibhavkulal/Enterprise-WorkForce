package com.enterprise.workforce.service.impl;

import com.enterprise.workforce.entity.Role;
import com.enterprise.workforce.enums.RoleType;
import com.enterprise.workforce.repository.RoleRepository;
import com.enterprise.workforce.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role findByName(RoleType name) {
        return roleRepository.findByName(name).orElseThrow(() -> new RuntimeException("Role not Found"));
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
