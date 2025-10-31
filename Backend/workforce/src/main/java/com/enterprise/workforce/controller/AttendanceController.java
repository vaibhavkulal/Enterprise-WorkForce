package com.enterprise.workforce.controller;

import com.enterprise.workforce.entity.Attendance;
import com.enterprise.workforce.entity.Employee;
import com.enterprise.workforce.enums.AttendanceStatus;
import com.enterprise.workforce.repository.AttendanceRepository;
import com.enterprise.workforce.repository.EmployeeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance Management", description = "APIs for employee check-in, check-out, and attendance tracking")
public class AttendanceController {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    @Operation(summary = "Record employee check-in")
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','ADMIN')")
    @PostMapping("/checkin/{empId}")
    public ResponseEntity<?> checkIn(@PathVariable Long empId) {
        Employee emp = employeeRepository.findById(empId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Employee not found"));

        boolean alreadyCheckedIn = attendanceRepository.existsByEmployeeAndDate(emp, LocalDate.now());
        if (alreadyCheckedIn) {
            return ResponseEntity.status(BAD_REQUEST).body(Map.of("error", "Already checked in today"));
        }

        Attendance attendance = Attendance.builder()
                .employee(emp)
                .checkInTime(LocalDateTime.now())
                .date(LocalDate.now())
                .status(AttendanceStatus.PRESENT)
                .build();

        attendanceRepository.save(attendance);
        return ResponseEntity.status(CREATED).body(Map.of(
                "message", "Check-in recorded successfully",
                "attendance", attendance
        ));
    }

    @Operation(summary = "Record employee check-out by employee ID (auto-detect today's record)")
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','ADMIN')")
    @PutMapping("/checkout/employee/{empId}")
    public ResponseEntity<?> checkOutByEmployeeId(@PathVariable Long empId) {
        Employee emp = employeeRepository.findById(empId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Employee not found"));

        Attendance attendance = attendanceRepository.findByEmployeeAndDate(emp, LocalDate.now())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No check-in record found for today"));

        if (attendance.getCheckOutTime() != null) {
            return ResponseEntity.status(BAD_REQUEST).body(Map.of("error", "Already checked out"));
        }

        attendance.setCheckOutTime(LocalDateTime.now());
        attendance.calculateTotalHours();
        attendanceRepository.save(attendance);

        return ResponseEntity.ok(Map.of(
                "message", "Check-out recorded successfully for employee",
                "attendance", attendance
        ));
    }
    @Operation(summary = "Record employee check-out")
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR','ADMIN')")
    @PutMapping("/checkout/{attendanceId}")
    public ResponseEntity<?> checkOut(@PathVariable Long attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Attendance record not found"));

        if (attendance.getCheckOutTime() != null) {
            return ResponseEntity.status(BAD_REQUEST).body(Map.of("error", "Already checked out"));
        }

        attendance.setCheckOutTime(LocalDateTime.now());
        attendance.calculateTotalHours();
        attendanceRepository.save(attendance);

        return ResponseEntity.ok(Map.of(
                "message", "Check-out recorded successfully",
                "attendance", attendance
        ));
    }

    @Operation(summary = "Get all attendance records for a specific employee")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping("/employee/{empId}")
    public ResponseEntity<?> getAttendanceByEmployee(@PathVariable Long empId) {
        Employee emp = employeeRepository.findById(empId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Employee not found"));

        List<Attendance> records = attendanceRepository.findByEmployee(emp);
        return ResponseEntity.ok(Map.of(
                "employeeId", empId,
                "attendanceRecords", records,
                "count", records.size()
        ));
    }

    @Operation(summary = "Get all attendance records")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllAttendance() {
        List<Attendance> allRecords = attendanceRepository.findAll();
        return ResponseEntity.ok(Map.of(
                "count", allRecords.size(),
                "records", allRecords
        ));
    }



    @Operation(summary = "Generate attendance summary report between date range (Admin/HR only)")
    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @GetMapping("/report")
    public ResponseEntity<?> getAttendanceReport(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {

        if (start.isAfter(end)) {
            return ResponseEntity.status(BAD_REQUEST).body(Map.of("error", "Start date cannot be after end date"));
        }

        List<Attendance> records = attendanceRepository.findByDateBetween(start, end);

        long total = records.size();
        long presentCount = records.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        long absentCount = records.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();
        long lateCount = records.stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count();

        return ResponseEntity.ok(Map.of(
                "startDate", start,
                "endDate", end,
                "totalRecords", total,
                "present", presentCount,
                "absent", absentCount,
                "late", lateCount
        ));
    }

    @Operation(summary = "Get monthly/weekly attendance stats for an employee")
    @PreAuthorize("hasAnyRole('HR','ADMIN','EMPLOYEE')")
    @GetMapping("/stats/{empId}")
    public ResponseEntity<?> getEmployeeAttendanceStats(@PathVariable Long empId) {
        Employee emp = employeeRepository.findById(empId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Employee not found"));

        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate weekStart = today.minusDays(7);

        List<Attendance> monthly = attendanceRepository.findByEmployeeAndDateBetween(emp, monthStart, today);
        List<Attendance> weekly = attendanceRepository.findByEmployeeAndDateBetween(emp, weekStart, today);

        long monthPresent = monthly.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        long monthAbsent = monthly.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();
        long weekPresent = weekly.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        long weekAbsent = weekly.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();

        return ResponseEntity.ok(Map.of(
                "employeeId", empId,
                "weekly", Map.of(
                        "present", weekPresent,
                        "absent", weekAbsent,
                        "totalDays", weekly.size()
                ),
                "monthly", Map.of(
                        "present", monthPresent,
                        "absent", monthAbsent,
                        "totalDays", monthly.size()
                )
        ));
    }

}
