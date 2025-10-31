package com.enterprise.workforce.repository;

import com.enterprise.workforce.entity.Employee;
import com.enterprise.workforce.entity.LeaveRequest;
import com.enterprise.workforce.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployee(Employee employee);

    List<LeaveRequest> findByStatus(LeaveStatus status);
}
