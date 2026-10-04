package com.MiniProject.backend.service;

import com.MiniProject.backend.model.User;
import com.MiniProject.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Signup -> Add new user
    public String signup(User user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            return "Invalid username or password!";
        }
        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser != null) {
            return "Username already exists!";
        }
        userRepository.save(user);
        return "User registered successfully!";
    }

    // Login -> Verify credentials
    public String login(User user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            return "Invalid username or password!";
        }
        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser != null && existingUser.getPassword().equals(user.getPassword())) {
            return "Login successful!";
        }
        return "Invalid username or password!";
    }

    // Get all usernames
    public List<String> getAllUsernames() {
        return userRepository.findAll()
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
    }
}
