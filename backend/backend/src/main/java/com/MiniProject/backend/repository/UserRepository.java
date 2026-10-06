package com.MiniProject.backend.repository;

import com.MiniProject.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom finder method to check if username exists or to fetch a user by name
    User findByUsername(String username);
}
