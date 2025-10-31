package com.enterprise.workforce.config;

import com.enterprise.workforce.entity.Department;
import com.enterprise.workforce.entity.Role;
import com.enterprise.workforce.entity.User;
import com.enterprise.workforce.enums.RoleType;
import com.enterprise.workforce.repository.DepartmentRepository;
import com.enterprise.workforce.repository.RoleRepository;
import com.enterprise.workforce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // ✅ Seed roles
        for (RoleType type : RoleType.values()) {
            roleRepository.findByName(type).orElseGet(() ->
                    roleRepository.save(Role.builder().name(type).build())
            );
        }

        // ✅ Seed departments
        if (departmentRepository.count() == 0) {
            List<Department> depths = List.of(
                    Department.builder().name("Engineering").description("Handles all tech and software").build(),
                    Department.builder().name("HR").description("Manages employees and recruitment").build(),
                    Department.builder().name("Finance").description("Manages payroll and budgets").build()
            );
            departmentRepository.saveAll(depths);
        }


        // ✅ Seed users if not exist
        if (userRepository.findByUsername("admin").isEmpty()) {
            Role adminRole = roleRepository.findByName(RoleType.ADMIN).orElseThrow();
            User admin = User.builder()
                    .username("admin")
                    .email("admin@company.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(adminRole)
                    .active(true)
                    .build();
            userRepository.save(admin);
        }

        if (userRepository.findByUsername("hr").isEmpty()) {
            Role hrRole = roleRepository.findByName(RoleType.HR).orElseThrow();
            User hr = User.builder()
                    .username("hr")
                    .email("hr@company.com")
                    .password(passwordEncoder.encode("Hr@123"))
                    .role(hrRole)
                    .active(true)
                    .build();
            userRepository.save(hr);
        }

        System.out.println("✅ Default roles, departments, and users seeded successfully!");
    }
}
