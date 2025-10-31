package com.enterprise.workforce.service;

import com.enterprise.workforce.entity.Employee;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    List<Employee> getAllEmployees();

    Optional<Employee> getEmployeeByID(Long id);

    Employee saveEmployee(Employee employee);

    long countEmployees();

    Employee updateProfile(String username, Employee updatedEmployee);

//    String uploadOwnPhoto(String username, MultipartFile file);

    Employee updateEmployee(Long id, Employee employee);

    Employee findByUsername(String username);

    void deleteEmployee(Long id);

    String uploadOwnPhoto(String username, MultipartFile file);
}
