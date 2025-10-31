package com.enterprise.workforce.service;

import com.enterprise.workforce.entity.Role;
import com.enterprise.workforce.enums.RoleType;

import java.util.List;

public interface RoleService {
    Role findByName(RoleType name);

    List<Role> getAllRoles();
}
