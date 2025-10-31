package com.enterprise.workforce.service;

import com.enterprise.workforce.entity.Department;
import com.enterprise.workforce.entity.Employee;
import org.springframework.data.domain.Page;

import java.util.List;

public interface HRService {
    Employee createEmployee(Long deptId, Employee employee);
    Employee updateEmployee(Long id, Employee updatedEmployee);
    void deleteEmployee(Long id);
    Page<Employee> getAllEmployees(int page, int size, String search);
    List<Employee> getEmployeesByDepartment(Long deptId);
    void activateEmployee(Long id);
    void deactivateEmployee(Long id);
    Employee getEmployeeById(Long id);
    long getTotalEmployeeCount();
    long getActiveEmployeeCount();
    long getInactiveEmployeeCount();
    List<Department> getAllDepartments();


}
