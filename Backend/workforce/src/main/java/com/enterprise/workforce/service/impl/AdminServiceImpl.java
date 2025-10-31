package com.enterprise.workforce.service.impl;

import com.enterprise.workforce.entity.Role;
import com.enterprise.workforce.entity.User;
import com.enterprise.workforce.enums.RoleType;
import com.enterprise.workforce.repository.EmployeeRepository;
import com.enterprise.workforce.repository.RoleRepository;
import com.enterprise.workforce.repository.UserRepository;
import com.enterprise.workforce.service.AdminService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public User createUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        RoleType roleType = user.getRole() != null ? user.getRole().getName() : RoleType.EMPLOYEE;
        Role role = roleRepository.findByName(roleType).orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleType));

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(role);
        user.setActive(true);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        employeeRepository.findByUser(user).ifPresent(employeeRepository::delete);

        userRepository.delete(user);
    }

}
