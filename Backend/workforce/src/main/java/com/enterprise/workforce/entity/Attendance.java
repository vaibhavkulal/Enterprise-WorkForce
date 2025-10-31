package com.enterprise.workforce.entity;

import com.enterprise.workforce.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Each record corresponds to one work day
    private LocalDate date;

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    private Double totalHours; // auto-calculated after checkout

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status; // PRESENT, ABSENT, LATE, etc.

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    /**
     * Automatically set date if not provided
     */
    @PrePersist
    public void onCreate() {
        if (this.date == null) {
            this.date = LocalDate.now();
        }
    }

    /**
     * Utility method to calculate total hours
     */
    public void calculateTotalHours() {
        if (checkInTime != null && checkOutTime != null) {
            Duration duration = Duration.between(checkInTime, checkOutTime);
            this.totalHours = duration.toMinutes() / 60.0;
        }
    }
}
