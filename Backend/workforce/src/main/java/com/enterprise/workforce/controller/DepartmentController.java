package com.enterprise.workforce.controller;

import com.enterprise.workforce.entity.Department;
import com.enterprise.workforce.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/department")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3333")
@Tag(name = "Department Management", description = "APIs for managing departments done by HR, ADMIN")
public class DepartmentController {

    private final DepartmentService departmentService;

    // Create Department (goes through Auditable service)
    @Operation(summary = "Create a new department")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PostMapping("/create")
    public ResponseEntity<?> createDepartment(@RequestBody Department department) {
        try {
            Department saved = departmentService.createDepartment(department);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Department created successfully", "department", saved));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to create department"));
        }
    }

    // Update Department (optional audit if needed)
    @Operation(summary = "Update an existing department by ID")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable Long id, @RequestBody Department updatedDepartment) {
        try {
            Department saved = departmentService.updateDepartment(id, updatedDepartment);
            return ResponseEntity.ok(Map.of("message", "Department updated successfully", "department", saved));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error updating department"));
        }
    }

    // Delete Department (goes through Auditable service)
    @Operation(summary = "Delete a department by ID")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        try {
            departmentService.deleteDepartment(id);
            return ResponseEntity.ok(Map.of("message", "Department deleted successfully"));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error deleting department"));
        }
    }

    // Get All Departments
    @Operation(summary = "Get all departments")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(Map.of("count", departments.size(), "departments", departments));
    }

    // Get Department by ID
    @Operation(summary = "Get department by ID")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartmentById(@PathVariable Long id) {
        Department department = departmentService.getAllDepartments()
                .stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));
        return ResponseEntity.ok(Map.of("department", department));
    }
}
