package com.enterprise.workforce.service.impl;

import com.enterprise.workforce.annotations.Auditable;
import com.enterprise.workforce.entity.Department;
import com.enterprise.workforce.entity.Employee;
import com.enterprise.workforce.entity.User;
import com.enterprise.workforce.enums.EmploymentStatus;
import com.enterprise.workforce.repository.DepartmentRepository;
import com.enterprise.workforce.repository.EmployeeRepository;
import com.enterprise.workforce.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final FileStorageService fileStorageService;
    private final DepartmentRepository departmentRepository;

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public long countEmployees() {
        return employeeRepository.count();
    }

    @Override
    public Optional<Employee> getEmployeeByID(Long id) {
        return employeeRepository.findById(id);
    }

    @Override
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public Employee findByUsername(String username) {
        return employeeRepository.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Employee not found with username: " + username));
    }

    @Override
    public Employee updateProfile(String username, Employee updatedEmployee) {
        Employee existing = findByUsername(username);

        existing.setFirstName(updatedEmployee.getFirstName());
        existing.setLastName(updatedEmployee.getLastName());
        existing.setPhone(updatedEmployee.getPhone());
        existing.setAddress(updatedEmployee.getAddress());
        existing.setDateOfBirth(updatedEmployee.getDateOfBirth());
        existing.setGender(updatedEmployee.getGender());
        existing.setCity(updatedEmployee.getCity());
        existing.setState(updatedEmployee.getState());
        existing.setPostalCode(updatedEmployee.getPostalCode());

        return employeeRepository.save(existing);
    }

    @Override
    @Auditable(action = "UPDATE_EMPLOYEE", entity = "Employee")
    public Employee updateEmployee(Long id, Employee employeeDetails) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setGender(employeeDetails.getGender());
        employee.setDateOfBirth(employeeDetails.getDateOfBirth());
        employee.setPhone(employeeDetails.getPhone());
        employee.setAddress(employeeDetails.getAddress());
        employee.setCity(employeeDetails.getCity());
        employee.setState(employeeDetails.getState());
        employee.setPostalCode(employeeDetails.getPostalCode());
        employee.setProfileImage(employeeDetails.getProfileImage());
        employee.setJoinDate(employeeDetails.getJoinDate());
        employee.setBaseSalary(employeeDetails.getBaseSalary());
        employee.setDepartment(employeeDetails.getDepartment());
        employee.setStatus(employeeDetails.getStatus());

        return employeeRepository.save(employee);
    }

    @Override
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
    @Auditable(action = "EMPLOYEE_UPDATE_Photo", entity = "Employee")
    public String uploadOwnPhoto(String username, MultipartFile file){
        Employee emp = employeeRepository.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("user found"));
        String stored = fileStorageService.storeEmployeeFile(emp.getId(), file);
        emp.setProfileImage(stored);
        employeeRepository.save(emp);
        return stored;
    }
    public Employee createEmployeeForUser(User user, Long departmentId, Map<String, Object> details) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));

        Employee employee = new Employee();
        employee.setUser(user);
        employee.setDepartment(department);
        employee.setFirstName((String) details.getOrDefault("firstName", user.getUsername()));
        employee.setJoinDate(LocalDate.now());
        employee.setStatus(EmploymentStatus.ACTIVE);

        return employeeRepository.save(employee);
    }

}
