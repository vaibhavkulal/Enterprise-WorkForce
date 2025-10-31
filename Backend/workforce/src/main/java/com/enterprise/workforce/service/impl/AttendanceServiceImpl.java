package com.enterprise.workforce.service.impl;

import com.enterprise.workforce.entity.Attendance;
import com.enterprise.workforce.entity.Employee;
import com.enterprise.workforce.repository.AttendanceRepository;
import com.enterprise.workforce.repository.EmployeeRepository;
import com.enterprise.workforce.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Attendance markAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    @Override
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    @Override
    public List<Attendance> getAttendanceByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Employee Not Found"));
        return attendanceRepository.findByEmployee(employee);
    }
}
