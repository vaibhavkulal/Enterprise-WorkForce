package com.enterprise.workforce.repository;

import com.enterprise.workforce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByOtp(String otp);
    @Query("SELECT u FROM User u WHERE u.id NOT IN (SELECT e.user.id FROM Employee e WHERE e.user IS NOT NULL)")
    List<User> findUsersWithoutEmployee();


}
