package com.enterprise.workforce.repository;

import com.enterprise.workforce.entity.Attendance;
import com.enterprise.workforce.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByEmployee(Employee employee);
    boolean existsByEmployeeAndDate(Employee employee, LocalDate date);
    Optional<Attendance> findByEmployeeAndDate(Employee employee, LocalDate date);
    List<Attendance> findByDateBetween(LocalDate start, LocalDate end);
    List<Attendance> findByEmployeeAndDateBetween(Employee employee, LocalDate start, LocalDate end);

}
