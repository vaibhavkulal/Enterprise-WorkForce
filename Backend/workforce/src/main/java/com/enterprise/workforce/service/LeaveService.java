package com.enterprise.workforce.service;

import com.enterprise.workforce.entity.LeaveRequest;

import java.util.List;

public interface LeaveService {
    LeaveRequest applyLeave(LeaveRequest request);

    LeaveRequest approveLeave(Long id);

    LeaveRequest rejectLeave(Long id);

    List<LeaveRequest> getAllLeaves();

    List<LeaveRequest> getAllLeaveRequests();

    List<LeaveRequest> getLeavesByEmployee(Long empId);
}
