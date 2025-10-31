package com.enterprise.workforce.service.impl;

import com.enterprise.workforce.dto.CreateUserRequest;
import com.enterprise.workforce.entity.Role;
import com.enterprise.workforce.entity.User;
import com.enterprise.workforce.enums.RoleType;
import com.enterprise.workforce.repository.RoleRepository;
import com.enterprise.workforce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    public User createUser(CreateUserRequest req) {
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

//        RoleType roleType = req.getRole() != null ? req.getRole() : RoleType.EMPLOYEE;
        RoleType roleType;
        try {
            roleType = RoleType.valueOf(req.getRole().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid role: " + req.getRole());
        }
        Role role = roleRepository.findByName(RoleType.valueOf(roleType.name()))
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleType));

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(role)  // ✅ assign Role entity
                .active(true)
                .build();

        return userRepository.save(user);
    }

    public User createUser(User user) {

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        // ✅ Determine Role
        RoleType roleType = (user.getRole() != null && user.getRole().getName() != null)
                ? user.getRole().getName()
                : RoleType.EMPLOYEE;

        // ✅ Fetch from DB
        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleType));

        // ✅ Encode Password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(role);
        user.setActive(true);

        return userRepository.save(user);
    }
    public List<User> getUnassignedUsers() {
        return userRepository.findUsersWithoutEmployee();
    }

}
