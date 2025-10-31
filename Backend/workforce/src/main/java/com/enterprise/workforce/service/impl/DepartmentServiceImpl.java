package com.enterprise.workforce.service.impl;

import com.enterprise.workforce.annotations.Auditable;
import com.enterprise.workforce.entity.Department;
import com.enterprise.workforce.repository.DepartmentRepository;
import com.enterprise.workforce.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {


    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    @Auditable(action = "CREATE_DEPARTMENT", entity = "Department")
    public Department createDepartment(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    public Department updateDepartment(Long id, Department department) {
        Department dept = departmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Department not found"));
        dept.setName(department.getName());
        dept.setDescription(department.getDescription());
        return departmentRepository.save(dept);
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    @Auditable(action = "DELETE_DEPARTMENT", entity = "Department")
    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }

    @Override
    public long countDepartments() {
        return departmentRepository.count();
    }

}
