package com.MiniProject.backend.controller;

import com.MiniProject.backend.model.User;
import com.MiniProject.backend.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*") // frontend CORS support
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Signup endpoint (supports both /signup and /users/signup)
    @PostMapping({"/signup", "/users/signup"})
    public String signup(@RequestBody User user) {
        return userService.signup(user);
    }

    // Login endpoint (supports both /login and /users/login)
    @PostMapping({"/login", "/users/login"})
    public String login(@RequestBody User user) {
        return userService.login(user);
    }

    // Get usernames endpoint (supports both /users and /users/all)
    @GetMapping({"/users", "/users/all"})
    public List<String> getAllUsernames() {
        return userService.getAllUsernames();
    }
}
