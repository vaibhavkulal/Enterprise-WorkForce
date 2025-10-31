package com.enterprise.workforce.controller;

import com.enterprise.workforce.entity.Employee;
import com.enterprise.workforce.entity.LeaveRequest;
import com.enterprise.workforce.entity.Payroll;
import com.enterprise.workforce.service.EmployeeService;
import com.enterprise.workforce.service.LeaveService;
import com.enterprise.workforce.service.PayrollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "Endpoints for employees to access their dashboard, profile, payroll, and leave applications")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final PayrollService payrollService;
    private final LeaveService leaveService;

    @Operation(summary = "Employee dashboard overview", description = "Displays summary of attendance, payroll, and leave info for the logged-in employee.")
    @ApiResponse(responseCode = "200", description = "Dashboard data retrieved successfully")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/dashboard")
    public ResponseEntity<?> employeeDashboard(Principal principal) {
        String username = principal.getName();
        Employee emp = employeeService.findByUsername(username);

        Map<String, Object> dashboardData = Map.of("employeeName", emp.getFirstName() + " " + emp.getLastName(), "department", emp.getDepartment().getName(), "totalPayrolls", payrollService.getPayrollsByEmployee(emp.getId()).size(), "leavesPending", leaveService.getLeavesByEmployee(emp.getId()).stream().filter(l -> l.getStatus().name().equalsIgnoreCase("PENDING")).count(), "status", emp.getStatus().name());

        return ResponseEntity.ok(dashboardData);
    }

    @Operation(summary = "View employee profile", description = "Fetches detailed profile information for the logged-in employee.")
    @ApiResponse(responseCode = "200", description = "Employee profile retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Employee.class)))
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/profile")
    public ResponseEntity<Employee> getProfile(Principal principal) {
        String username = principal.getName();
        return ResponseEntity.ok(employeeService.findByUsername(username));
    }

    @Operation(summary = "Update employee profile", description = "Allows an employee to update their personal profile details.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Profile updated successfully"), @ApiResponse(responseCode = "400", description = "Invalid request data")})
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/profile/update")
    public ResponseEntity<?> updateProfile(@RequestBody Employee updatedEmployee, Principal principal) {
        String username = principal.getName();
        Employee updated = employeeService.updateProfile(username, updatedEmployee);
        return ResponseEntity.ok(Map.of("message", "Profile updated successfully", "employee", updated));
    }

    @Operation(summary = "Apply for leave", description = "Allows employee to submit a leave request.")
    @ApiResponse(responseCode = "200", description = "Leave request submitted successfully")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/leave/apply")
    public ResponseEntity<?> applyLeave(@RequestBody LeaveRequest leaveRequest, Principal principal) {
        String username = principal.getName();
        Employee emp = employeeService.findByUsername(username);
        leaveRequest.setEmployee(emp);
        LeaveRequest created = leaveService.applyLeave(leaveRequest);
        return ResponseEntity.ok(Map.of("message", "Leave applied successfully", "leaveRequest", created));
    }

    @Operation(summary = "View leave status", description = "Fetches all leave applications and their current approval status for the logged-in employee.")
    @ApiResponse(responseCode = "200", description = "Leave records retrieved successfully")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/leave/status")
    public ResponseEntity<List<LeaveRequest>> getLeaveStatus(Principal principal) {
        String username = principal.getName();
        Employee emp = employeeService.findByUsername(username);
        return ResponseEntity.ok(leaveService.getLeavesByEmployee(emp.getId()));
    }

    @Operation(summary = "View payroll history", description = "Displays all payroll records (approved or pending) for the logged-in employee.")
    @ApiResponse(responseCode = "200", description = "Payroll records retrieved successfully")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/payrolls")
    public ResponseEntity<List<Payroll>> viewPayrolls(Principal principal) {
        String username = principal.getName();
        Employee emp = employeeService.findByUsername(username);
        return ResponseEntity.ok(payrollService.getPayrollsByEmployee(emp.getId()));
    }

    @Operation(summary = "Upload or update employee profile photo", description = "Allows employee to upload or update their own profile photo.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Profile photo uploaded successfully"), @ApiResponse(responseCode = "400", description = "Invalid file upload")})
    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/profile/upload-photo")
    public ResponseEntity<?> uploadProfilePhoto(@RequestParam("file") MultipartFile file, Principal principal) {

        String username = principal.getName();
        String message = employeeService.uploadOwnPhoto(username, file);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/profile/upload-own-photo")
    public ResponseEntity<?> uploadOwnPhoto(@RequestParam("file") MultipartFile file, Principal principal){
        String username = principal.getName();
        // employeeService will resolve user -> employeeId
        String storagePath = employeeService.uploadOwnPhoto(username, file);
        return ResponseEntity.ok(Map.of("path", storagePath));
    }



}
