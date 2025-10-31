package com.enterprise.workforce.controller;

import com.enterprise.workforce.dto.AssignDepartmentRequest;
import com.enterprise.workforce.entity.Employee;
import com.enterprise.workforce.entity.User;
import com.enterprise.workforce.service.HRService;
import com.enterprise.workforce.service.impl.EmployeeServiceImpl;
import com.enterprise.workforce.service.impl.HRServiceImpl;
import com.enterprise.workforce.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.enterprise.workforce.entity.LeaveRequest;
import com.enterprise.workforce.service.LeaveService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hr")
@RequiredArgsConstructor
@Tag(name = "HR Management", description = "APIs for HR to manage employees and view dashboard data")
public class HRController {

    private final HRService hrService;
    private final LeaveService leaveService;
    private  final HRServiceImpl hrService1;
    private final UserService userService;
    private final EmployeeServiceImpl employeeService;

    @Operation(summary = "Assign department to employee", description = "HR can assign an employee to a department")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department assigned successfully"),
            @ApiResponse(responseCode = "404", description = "Employee or Department not found")
    })
    @PreAuthorize("hasRole('HR')")
    @PostMapping("/assign-department")
    public ResponseEntity<?> assignDepartment(@RequestBody AssignDepartmentRequest request) {
        Employee updatedEmployee = hrService1.assignDepartment(request);
        return ResponseEntity.ok(updatedEmployee);
    }
    //  Create Employee + Auto User Account
    @Operation(summary = "Create a new Employee and auto-generate login credentials")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @PostMapping("/employees/create/{deptId}")
    public ResponseEntity<?> createEmployee(
            @PathVariable Long deptId,
            @RequestBody Employee employee
    ) {
        Employee created = hrService.createEmployee(deptId, employee);
        return ResponseEntity.ok(Map.of(
                "message", "Employee created successfully",
                "employee", created
        ));
    }

    //  HR Dashboard Summary
    @Operation(summary = "Get HR dashboard metrics (total, active, inactive employees)")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<?> getHRDashboard() {
        long totalEmployees = hrService.getTotalEmployeeCount();
        long activeEmployees = hrService.getActiveEmployeeCount();
        long inactiveEmployees = hrService.getInactiveEmployeeCount();

        return ResponseEntity.ok(Map.of(
                "totalEmployees", totalEmployees,
                "activeEmployees", activeEmployees,
                "inactiveEmployees", inactiveEmployees
        ));
    }

    //  Update Employee Details
    @Operation(summary = "Update an existing employee by ID")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @PutMapping("/employees/{id}")
    public ResponseEntity<?> updateEmployee(
            @PathVariable Long id,
            @RequestBody Employee updatedEmployee
    ) {
        Employee updated = hrService.updateEmployee(id, updatedEmployee);
        return ResponseEntity.ok(Map.of(
                "message", "Employee updated successfully",
                "employee", updated
        ));
    }

    //  Delete Employee
    @Operation(summary = "Delete an employee by ID")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @DeleteMapping("/employees/delete/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        hrService.deleteEmployee(id);
        return ResponseEntity.ok(Map.of("message", "Employee deleted successfully"));
    }

    //  Get All Employees (Paginated)
    @Operation(summary = "Get all employees with optional search")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping("/employees")
    public ResponseEntity<Page<Employee>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(hrService.getAllEmployees(page, size, search));
    }

    //  Get Employees by Department
    @Operation(summary = "Get all employees under a specific department")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping("/employees/department/{deptId}")
    public ResponseEntity<List<Employee>> getEmployeesByDepartment(@PathVariable Long deptId) {
        return ResponseEntity.ok(hrService.getEmployeesByDepartment(deptId));
    }

    //  Activate Employee
    @Operation(summary = "Activate a specific employee account")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @PutMapping("/employees/activate/{id}")
    public ResponseEntity<Map<String, String>> activateEmployee(@PathVariable Long id) {
        hrService.activateEmployee(id);
        return ResponseEntity.ok(Map.of("message", "Employee activated successfully"));
    }

    //  Deactivate Employee
    @Operation(summary = "Deactivate a specific employee account")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @PutMapping("/employees/deactivate/{id}")
    public ResponseEntity<Map<String, String>> deactivateEmployee(@PathVariable Long id) {
        hrService.deactivateEmployee(id);
        return ResponseEntity.ok(Map.of("message", "Employee deactivated successfully"));
    }

    //  Get Employee by ID
    @Operation(summary = "Get a specific employee by ID")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping("/employees/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(hrService.getEmployeeById(id));
    }


    @Operation(summary = "Approve a leave request")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @PutMapping("/leave/approve/{id}")
    public ResponseEntity<Map<String, String>> approveLeave(@PathVariable Long id) {
        leaveService.approveLeave(id);
        return ResponseEntity.ok(Map.of("message", "Leave request approved successfully"));
    }

    /**
     * Reject a leave request by ID (HR/Admin)
     */
    @Operation(summary = "Reject a leave request")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @PutMapping("/leave/reject/{id}")
    public ResponseEntity<Map<String, String>> rejectLeave(@PathVariable Long id) {
        leaveService.rejectLeave(id);
        return ResponseEntity.ok(Map.of("message", "Leave request rejected successfully"));
    }

    /**
     * Get all leave requests (pending, approved, rejected)
     */
    @Operation(summary = "Get all leave requests (for HR/Admin)")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping("/leave/all")
    public ResponseEntity<List<LeaveRequest>> getAllLeaveRequests() {
        List<LeaveRequest> leaves = leaveService.getAllLeaveRequests();
        return ResponseEntity.ok(leaves);
    }


    @PostMapping("/employees/create/{departmentId}/{userId}")
    public ResponseEntity<?> assignUserToEmployee(
            @PathVariable Long departmentId,
            @PathVariable Long userId,
            @RequestBody Map<String, Object> employeeDetails) {

        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }

            Employee employee = employeeService.createEmployeeForUser(user, departmentId, employeeDetails);
            return ResponseEntity.ok(Map.of(
                    "message", "Employee created successfully",
                    "employeeId", employee.getId(),
                    "user", user.getUsername()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to create employee", "details", e.getMessage()));
        }
    }

    //  Fetch all available users (not yet assigned as employees)
    @GetMapping("/users/unassigned")
    public ResponseEntity<?> getUnassignedUsers() {
        return ResponseEntity.ok(userService.getUnassignedUsers());
    }
    @Operation(summary = "Get all departments", description = "Fetches all departments for HR employee assignment")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping("/departments")
    public ResponseEntity<?> getAllDepartments() {
        try {
            return ResponseEntity.ok(hrService.getAllDepartments());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to load departments", "details", e.getMessage()));
        }
    }

}
