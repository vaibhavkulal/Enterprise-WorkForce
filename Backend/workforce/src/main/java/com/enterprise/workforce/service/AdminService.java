package com.enterprise.workforce.service;

import com.enterprise.workforce.entity.User;
import java.util.List;

public interface AdminService {
    User createUser(User user);
    List<User> getAllUsers();
    void deleteUser(Long id);
}
