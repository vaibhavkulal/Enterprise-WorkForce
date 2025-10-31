package com.enterprise.workforce.controller;

import com.enterprise.workforce.annotations.Auditable;
import com.enterprise.workforce.dto.CreateUserRequest;
import com.enterprise.workforce.entity.AuditLog;
import com.enterprise.workforce.entity.Payroll;
import com.enterprise.workforce.entity.User;
import com.enterprise.workforce.repository.AuditLogRepository;
import com.enterprise.workforce.service.AdminService;
import com.enterprise.workforce.service.DepartmentService;
import com.enterprise.workforce.service.EmployeeService;
import com.enterprise.workforce.service.PayrollService;
import com.enterprise.workforce.service.impl.AuditLogService;
import com.enterprise.workforce.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3333")
@Tag(name = "Admin Management", description = "Endpoints for Admin operations like user, department, and payroll management")
public class AdminController {

    private final AdminService adminService;
    private final AuditLogService auditLogService;
    private final PayrollService payrollService;
    @Autowired
    private final EmployeeService employeeService;
    @Autowired
    private AuditLogRepository auditLogRepository;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private UserService userService;

    @Operation(summary = "Get Admin Dashboard Summary", description = "Returns overall counts for users, employees, departments, and payroll statuses.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Dashboard summary retrieved successfully"), @ApiResponse(responseCode = "500", description = "Failed to fetch dashboard data")})
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardSummary() {
        try {
            long totalUsers = adminService.getAllUsers().size();
            long totalEmployees = employeeService.countEmployees();
            long totalDepartments = departmentService.countDepartments();
            long totalPayrolls = payrollService.countAllPayrolls();
            long pendingPayrolls = payrollService.countPendingPayrolls();
            long approvedPayrolls = payrollService.countApprovedPayrolls();

            return ResponseEntity.ok(Map.of("totalUsers", totalUsers, "totalEmployees", totalEmployees, "totalDepartments", totalDepartments, "totalPayrolls", totalPayrolls, "pendingPayrolls", pendingPayrolls, "approvedPayrolls", approvedPayrolls));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to fetch dashboard data"));
        }
    }

    @Operation(summary = "Create a new user", description = "Allows admin to create a new user with assigned role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user data or role"),
            @ApiResponse(responseCode = "500", description = "Unexpected error while creating user")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users")
    public ResponseEntity<Map<String, String>> createUser(@RequestBody CreateUserRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            userService.createUser(request); // call service
            response.put("message", "User created successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("error", "Unexpected error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }


    @Operation(summary = "Fetch all users", description = "Retrieves a list of all registered users.")
    @ApiResponse(responseCode = "200", description = "Fetched all users successfully")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @Operation(summary = "Delete user by ID", description = "Deletes a specific user using their ID.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "User deleted successfully"), @ApiResponse(responseCode = "400", description = "User not found"), @ApiResponse(responseCode = "500", description = "Error deleting user")})
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    @Auditable(action = "DELETE_USER", entity = "User")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            adminService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully", "userId", id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error deleting user"));
        }
    }

    @Operation(summary = "View all payrolls (paginated)", description = "Returns paginated payroll data for review.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Fetched all payrolls successfully"), @ApiResponse(responseCode = "500", description = "Failed to fetch payroll data")})
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/payrolls")
    public ResponseEntity<?> getAllPayrolls(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

        try {
            Page<Payroll> payrollPage = payrollService.getAllPayrolls(PageRequest.of(page, size));
            return ResponseEntity.ok(Map.of("content", payrollPage.getContent(), "currentPage", payrollPage.getNumber(), "totalPages", payrollPage.getTotalPages(), "totalItems", payrollPage.getTotalElements()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to fetch payroll data"));
        }
    }

    @Operation(summary = "Approve payroll", description = "Admin can approve payrolls generated by HR.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Payroll approved successfully"), @ApiResponse(responseCode = "400", description = "Invalid payroll ID or already approved"), @ApiResponse(responseCode = "500", description = "Error approving payroll")})
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/payrolls/approve/{id}")
    public ResponseEntity<?> approvePayroll(@PathVariable Long id) {
        try {
            Payroll approved = payrollService.approvePayroll(id);
            return ResponseEntity.ok(Map.of("message", "Payroll approved successfully", "payrollId", approved.getId(), "status", approved.getStatus().name()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to approve payroll"));
        }
    }

    @GetMapping("/audit/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLog>> getAllLogs(@RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return ResponseEntity.ok(auditLogService.getAllLogs(pageable));
    }


}
