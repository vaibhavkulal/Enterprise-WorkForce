package com.enterprise.workforce.controller;

import com.enterprise.workforce.entity.Payroll;
import com.enterprise.workforce.service.PayrollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
@Tag(name = "Payroll Management", description = "APIs for HR to create payroll and Admin to approve payrolls")
public class PayrollController {

    private final PayrollService payrollService;

    // HR: Generate Payroll for Employee
    @Operation(summary = "HR creates payroll for an employee (Pending Approval)")
    @PreAuthorize("hasRole('HR')")
    @PostMapping("/generate/{employeeId}")
    public ResponseEntity<?> generatePayroll(
            @PathVariable Long employeeId,
            @RequestBody Payroll payroll
    ) {
        Payroll created = payrollService.generatePayrollForEmployee(employeeId, payroll);
        return ResponseEntity.ok(Map.of(
                "message", "Payroll created successfully and awaiting Admin approval",
                "payroll", created
        ));
    }

    // Admin: Approve Payroll
    @Operation(summary = "Admin approves a payroll record")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approvePayroll(@PathVariable Long id) {
        Payroll approved = payrollService.approvePayroll(id);
        return ResponseEntity.ok(Map.of(
                "message", "Payroll approved successfully",
                "payroll", approved
        ));
    }

    // View All Payrolls (Paginated)
//    @Operation(summary = "View all payrolls (for HR/Admin)")
//    @PreAuthorize("hasAnyRole('HR','ADMIN')")
//    @GetMapping
//    public ResponseEntity<Page<Payroll>> getAllPayrolls(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        return ResponseEntity.ok(payrollService.getAllPayrolls(PageRequest.of(page, size)));
//    }

    // View All Payrolls (Paginated with Employee Info)
    @Operation(summary = "View all payrolls (for HR/Admin)")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPayrolls(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Payroll> payrollPage = payrollService.getAllPayrolls(PageRequest.of(page, size));

        // Use Map<String, Object> to avoid Serializable type mismatch
        List<Map<String, Object>> payrollList = payrollPage.getContent().stream().map(p -> {
            String employeeName = "-";
            if (p.getEmployee() != null) {
                String first = p.getEmployee().getFirstName() != null ? p.getEmployee().getFirstName() : "";
                String last = p.getEmployee().getLastName() != null ? p.getEmployee().getLastName() : "";
                employeeName = (first + " " + last).trim();
                if (employeeName.isEmpty()) employeeName = "-";
            }

            return Map.<String, Object>of(
                    "id", p.getId(),
                    "basicSalary", p.getBasicSalary(),
                    "bonus", p.getBonus(),
                    "tax", p.getTax(),
                    "totalPay", p.getTotalPay(),
                    "payDate", p.getPayDate(),
                    "status", p.getStatus().name(),
                    "employeeName", employeeName
            );
        }).toList();

        Map<String, Object> response = Map.of(
                "content", payrollList,
                "currentPage", payrollPage.getNumber(),
                "totalItems", payrollPage.getTotalElements(),
                "totalPages", payrollPage.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }


    // Payroll Dashboard
    @Operation(summary = "Get payroll summary dashboard (total, approved, pending)")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<?> getPayrollDashboard() {
        long total = payrollService.countAllPayrolls();
        long pending = payrollService.countPendingPayrolls();
        long approved = payrollService.countApprovedPayrolls();

        return ResponseEntity.ok(Map.of(
                "totalPayrolls", total,
                "pendingPayrolls", pending,
                "approvedPayrolls", approved
        ));
    }

    // [NEW] Get Payroll by ID
    @Operation(summary = "View single payroll details by ID (for Admin/HR)")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Payroll> getPayrollById(@PathVariable Long id) {
        Payroll payroll = payrollService.getPayrollById(id);
        return ResponseEntity.ok(payroll);
    }

    // [NEW] Get Payrolls for a Specific Employee
    @Operation(summary = "View payroll history for a specific employee (Admin/HR)")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping("/employee/{empId}")
    public ResponseEntity<List<Payroll>> getPayrollsByEmployee(@PathVariable Long empId) {
        List<Payroll> payrolls = payrollService.getPayrollsByEmployee(empId);
        return ResponseEntity.ok(payrolls);
    }

    // [NEW] Export Payroll Data (CSV)
    @Operation(summary = "Export payroll data as CSV (for Admin/HR)")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportPayrollData() {
        byte[] csvData = payrollService.exportPayrollsToCSV();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payroll-report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

}
