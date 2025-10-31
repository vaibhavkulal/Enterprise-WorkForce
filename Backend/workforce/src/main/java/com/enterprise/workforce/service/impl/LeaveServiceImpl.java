package com.enterprise.workforce.service.impl;

import com.enterprise.workforce.entity.Employee;
import com.enterprise.workforce.entity.LeaveRequest;
import com.enterprise.workforce.enums.LeaveStatus;
import com.enterprise.workforce.repository.EmployeeRepository;
import com.enterprise.workforce.repository.LeaveRequestRepository;
import com.enterprise.workforce.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Apply for leave — sets default status to PENDING.
     */
    @Override
    public LeaveRequest applyLeave(LeaveRequest request) {
        request.setStatus(LeaveStatus.PENDING);
        return leaveRequestRepository.save(request);
    }

    /**
     * Approve a leave request by ID.
     */
    @Override
    public LeaveRequest approveLeave(Long id) {
        LeaveRequest leave = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found with ID: " + id));
        leave.setStatus(LeaveStatus.APPROVED);
        return leaveRequestRepository.save(leave);
    }

    /**
     * Reject a leave request by ID.
     */
    @Override
    public LeaveRequest rejectLeave(Long id) {
        LeaveRequest leave = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found with ID: " + id));
        leave.setStatus(LeaveStatus.REJECTED);
        return leaveRequestRepository.save(leave);
    }

    /**
     * Get all leave requests in the system.
     */
    @Override
    public List<LeaveRequest> getAllLeaves() {
        return leaveRequestRepository.findAll();
    }

    /**
     * Get all leave requests (alias for getAllLeaves, used by HR/Admin dashboards).
     */
    @Override
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    /**
     * Get leave requests by employee ID.
     */
    @Override
    public List<LeaveRequest> getLeavesByEmployee(Long empId) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + empId));
        return leaveRequestRepository.findByEmployee(employee);
    }
}
