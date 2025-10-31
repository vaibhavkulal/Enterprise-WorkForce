package com.enterprise.workforce.service;

import com.enterprise.workforce.entity.Payroll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PayrollService {
    Payroll generatePayroll(Payroll payroll);

    Payroll generatePayrollForEmployee(Long employeeId, Payroll payroll);

    Page<Payroll> getAllPayrolls(Pageable pageable);

    List<Payroll> getPayrollsByEmployee(Long empId);

    Payroll approvePayroll(Long id);

    long countAllPayrolls();

    Payroll getPayrollById(Long id);

    byte[] exportPayrollsToCSV();

    long countPendingPayrolls();

    long countApprovedPayrolls();


}
