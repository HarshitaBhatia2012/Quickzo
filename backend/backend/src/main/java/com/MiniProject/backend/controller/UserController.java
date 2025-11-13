package com.MiniProject.backend.controller;

import com.MiniProject.backend.model.User;
import com.MiniProject.backend.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*") // frontend ke liye CORS allow
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Signup endpoint
    @PostMapping("/signup")
    public String signup(@RequestBody User user) {
        return userService.signup(user);
    }

    // Login endpoint
    @PostMapping("/login")
    public String login(@RequestBody User user) {
        return userService.login(user);
    }

    // Get usernames endpoint
    @GetMapping("/users")
    public List<String> getAllUsernames() {
        return userService.getAllUsernames();
    }
}
