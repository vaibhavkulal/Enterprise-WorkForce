package com.enterprise.workforce.repository;

import com.enterprise.workforce.entity.Department;
import com.enterprise.workforce.entity.Employee;
import com.enterprise.workforce.entity.User;
import com.enterprise.workforce.enums.EmploymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Page<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName, Pageable pageable);

//    Optional<Employee> findByUsername(String username);
    Optional<Employee> findByUser_Username(String username);

    List<Employee> findByDepartment(Department department);

    Optional<Employee> findByUser(User user);

    long countByStatus(EmploymentStatus status);

}
