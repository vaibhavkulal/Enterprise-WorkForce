package com.enterprise.workforce.repository;

import com.enterprise.workforce.entity.Role;
import com.enterprise.workforce.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}
