package com.enterprise.workforce.entity;

import com.enterprise.workforce.enums.EmploymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"department", "user"})
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "employees")
public class Employee extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String postalCode;

    private String profileImage; // store image path or URL

    private LocalDate joinDate;
    private Double baseSalary;

    @Enumerated(EnumType.STRING)
    private EmploymentStatus status = EmploymentStatus.ACTIVE;


    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

}
