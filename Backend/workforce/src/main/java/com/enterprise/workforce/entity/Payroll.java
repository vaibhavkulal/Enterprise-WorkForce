package com.enterprise.workforce.entity;

import com.enterprise.workforce.enums.PayrollStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payrolls")
public class Payroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double basicSalary;
    private Double bonus;
    private Double tax;
    private Double totalPay;
    private LocalDate payDate;

    @Enumerated(EnumType.STRING)
    private PayrollStatus status = PayrollStatus.PENDING_APPROVAL;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
