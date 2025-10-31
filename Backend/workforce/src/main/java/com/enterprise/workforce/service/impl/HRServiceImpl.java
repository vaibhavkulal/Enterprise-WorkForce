package com.enterprise.workforce.service.impl;

import com.enterprise.workforce.dto.AssignDepartmentRequest;
import com.enterprise.workforce.entity.Department;
import com.enterprise.workforce.entity.Employee;
import com.enterprise.workforce.entity.Role;
import com.enterprise.workforce.entity.User;
import com.enterprise.workforce.enums.EmploymentStatus;
import com.enterprise.workforce.enums.RoleType;
import com.enterprise.workforce.repository.DepartmentRepository;
import com.enterprise.workforce.repository.EmployeeRepository;
import com.enterprise.workforce.repository.RoleRepository;
import com.enterprise.workforce.repository.UserRepository;
import com.enterprise.workforce.service.HRService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HRServiceImpl implements HRService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
;

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }


    @Override
    public Employee createEmployee(Long deptId, Employee employee) {
        // 1️⃣ Get department
        Department department = departmentRepository.findById(deptId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        // 2️⃣ Create new User for Employee
        if (employee.getUser().getEmail() == null || employee.getUser().getEmail().isBlank()) {
            throw new RuntimeException("Employee email is required to create user credentials");
        }

        if (userRepository.findByEmail(employee.getUser().getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        Role empRole = roleRepository.findByName(RoleType.EMPLOYEE)
                .orElseThrow(() -> new RuntimeException("Employee role not found"));

        User user = new User();
        user.setUsername(employee.getUser().getEmail().split("@")[0]);
        user.setEmail(employee.getUser().getEmail());
        user.setPassword(passwordEncoder.encode("Welcome@123")); // default password
        user.setRole(empRole);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        // 3️⃣ Create Employee record
        employee.setUser(savedUser);
        employee.setDepartment(department);
        employee.setStatus(EmploymentStatus.ACTIVE);

        return employeeRepository.save(employee);
    }

    @Override
    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setFirstName(updatedEmployee.getFirstName());
        employee.setLastName(updatedEmployee.getLastName());
        employee.setPhone(updatedEmployee.getPhone());
        employee.setAddress(updatedEmployee.getAddress());
        employee.setCity(updatedEmployee.getCity());
        employee.setState(updatedEmployee.getState());
        employee.setPostalCode(updatedEmployee.getPostalCode());
        employee.setBaseSalary(updatedEmployee.getBaseSalary());
        employee.setProfileImage(updatedEmployee.getProfileImage());
        employee.setGender(updatedEmployee.getGender());

        return employeeRepository.save(employee);
    }

    @Override
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found");
        }
        employeeRepository.deleteById(id);
    }

    @Override
    public Page<Employee> getAllEmployees(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        if (search != null && !search.isBlank()) {
            return employeeRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(search, search, pageable);
        }
        return employeeRepository.findAll(pageable);
    }

    @Override
    public List<Employee> getEmployeesByDepartment(Long deptId) {
        Department department = departmentRepository.findById(deptId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        return employeeRepository.findByDepartment(department);
    }

    @Override
    public void activateEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        employee.setStatus(EmploymentStatus.ACTIVE);
        employeeRepository.save(employee);
    }

    @Override
    public void deactivateEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        employee.setStatus(EmploymentStatus.INACTIVE);
        employeeRepository.save(employee);
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    @Override
    public long getTotalEmployeeCount() {
        return employeeRepository.count();
    }

    @Override
    public long getActiveEmployeeCount() {
        return employeeRepository.countByStatus(EmploymentStatus.ACTIVE);
    }

    @Override
    public long getInactiveEmployeeCount() {
        return employeeRepository.countByStatus(EmploymentStatus.INACTIVE);
    }

    public Employee assignDepartment(AssignDepartmentRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));

        employee.setDepartment(department);
        return employeeRepository.save(employee);
    }
}
