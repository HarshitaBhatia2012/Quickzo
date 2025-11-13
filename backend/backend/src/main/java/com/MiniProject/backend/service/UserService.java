package com.MiniProject.backend.service;

import com.MiniProject.backend.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final List<User> users = new ArrayList<>();

    // Signup → Add new user
    public String signup(User user) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(user.getUsername())) {
                return "Username already exists!";
            }
        }
        users.add(user);
        return "User registered successfully!";
    }

    // Login → Verify credentials
    public String login(User user) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(user.getUsername()) &&
                    u.getPassword().equals(user.getPassword())) {
                return "Login successful!";
            }
        }
        return "Invalid username or password!";
    }

    // Get all usernames
    public List<String> getAllUsernames() {
        List<String> usernames = new ArrayList<>();
        for (User u : users) {
            usernames.add(u.getUsername());
        }
        return usernames;
    }
}
