package com.enterprise.workforce.service.impl;

import com.enterprise.workforce.dto.PayrollDTO;
import com.enterprise.workforce.entity.Employee;
import com.enterprise.workforce.entity.Payroll;
import com.enterprise.workforce.enums.PayrollStatus;
import com.enterprise.workforce.repository.EmployeeRepository;
import com.enterprise.workforce.repository.PayrollRepository;
import com.enterprise.workforce.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PayrollServiceImpl implements PayrollService {

    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Payroll approvePayroll(Long id) {
        Payroll payroll = payrollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payroll not found"));
        payroll.setStatus(PayrollStatus.APPROVED);
        return payrollRepository.save(payroll);
    }

    @Override
    public Payroll generatePayrollForEmployee(Long employeeId, Payroll payroll) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        double totalPay = payroll.getBasicSalary()
                + (payroll.getBonus() != null ? payroll.getBonus() : 0)
                - (payroll.getTax() != null ? payroll.getTax() : 0);

        payroll.setEmployee(employee);
        payroll.setTotalPay(totalPay);
        payroll.setPayDate(LocalDate.now());
        payroll.setStatus(PayrollStatus.PENDING_APPROVAL);

        return payrollRepository.save(payroll);
    }

    @Override
    public Payroll generatePayroll(Payroll payroll) {
        payroll.setStatus(PayrollStatus.PENDING_APPROVAL);
        return payrollRepository.save(payroll);
    }

    @Override
    public Page<Payroll> getAllPayrolls(Pageable pageable) {
        return payrollRepository.findAll(pageable);
    }

    @Override
    public Payroll getPayrollById(Long id) {
        return payrollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payroll not found"));
    }

    @Override
    public List<Payroll> getPayrollsByEmployee(Long empId) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return payrollRepository.findByEmployee(employee);
    }

    @Override
    public long countAllPayrolls() {
        return payrollRepository.count();
    }

    @Override
    public long countPendingPayrolls() {
        return payrollRepository.findByStatus(PayrollStatus.PENDING_APPROVAL).size();
    }

    @Override
    public long countApprovedPayrolls() {
        return payrollRepository.findByStatus(PayrollStatus.APPROVED).size();
    }

    @Override
    public byte[] exportPayrollsToCSV() {
        List<Payroll> payrolls = payrollRepository.findAll();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(outputStream)) {

            writer.println("ID,Employee Name,Basic Salary,Bonus,Tax,Total Pay,Status,Pay Date");

            for (Payroll p : payrolls) {
                writer.printf("%d,%s,%.2f,%.2f,%.2f,%.2f,%s,%s%n",
                        p.getId(),
                        p.getEmployee() != null ? p.getEmployee().getFirstName() : "N/A",
                        p.getBasicSalary(),
                        p.getBonus() != null ? p.getBonus() : 0,
                        p.getTax() != null ? p.getTax() : 0,
                        p.getTotalPay(),
                        p.getStatus(),
                        p.getPayDate());
            }

            writer.flush();
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error exporting payrolls to CSV", e);
        }
    }
    public List<PayrollDTO> getAllPayrolls() {
        return payrollRepository.findAll().stream()
                .map(p -> {
                    PayrollDTO dto = new PayrollDTO();
                    dto.setId(p.getId());
                    dto.setBasicSalary(p.getBasicSalary());
                    dto.setBonus(p.getBonus());
                    dto.setTax(p.getTax());
                    dto.setTotalPay(p.getTotalPay());
                    dto.setPayDate(p.getPayDate());
                    dto.setStatus(p.getStatus());
                    dto.setEmployeeName(p.getEmployee() != null ? p.getEmployee().getFirstName() : "-");
                    return dto;
                })
                .collect(Collectors.toList());
    }

}
