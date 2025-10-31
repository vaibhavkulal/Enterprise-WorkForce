package com.enterprise.workforce.service;

import com.enterprise.workforce.entity.Attendance;

import java.util.List;

public interface AttendanceService {
    Attendance markAttendance(Attendance attendance);

    List<Attendance> getAllAttendance();

    List<Attendance> getAttendanceByEmployee(Long employeeId);
}
