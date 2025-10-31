package com.enterprise.workforce.service;

import com.enterprise.workforce.entity.Department;

import java.util.List;

public interface DepartmentService {
    Department createDepartment(Department department);

    long countDepartments();

    Department updateDepartment(Long id, Department department);

    List<Department> getAllDepartments();

    void deleteDepartment(Long id);
}
