package com.enterprise.workforce.repository;

import com.enterprise.workforce.entity.Employee;
import com.enterprise.workforce.entity.Payroll;
import com.enterprise.workforce.enums.PayrollStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayrollRepository extends JpaRepository<Payroll,Long> {

    List<Payroll> findByEmployee(Employee employee);
    List<Payroll> findByStatus(PayrollStatus status);
}
