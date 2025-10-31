package com.enterprise.workforce.dto;

import com.enterprise.workforce.enums.PayrollStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PayrollDTO {
    private Long id;
    private Double basicSalary;
    private Double bonus;
    private Double tax;
    private Double totalPay;
    private LocalDate payDate;
    private PayrollStatus status;
    private String employeeName;

    // Derived fields for frontend
    public String getMonth() {
        return payDate != null ? payDate.getMonth().name() + " " + payDate.getYear() : "-";
    }

    public Double getAmount() {
        return totalPay != null ? totalPay : 0.0;
    }
}
