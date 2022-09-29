package com.demo.app.controller;

import com.demo.app.repository.User;
import com.demo.app.service.UserService;
import com.demo.app.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    private final JwtUtil jwtUtil = new JwtUtil();

    @PostMapping(value = "login", consumes = "application/json", produces = "application/json")
    public LoginResponse login(@RequestBody final UserLogin login) throws Exception {
        if (!userService.authenticate(login.name, login.password))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Invalid login. Please check your username and password.");
        return new LoginResponse(jwtUtil.generateToken(login.name));
    }

    private static class UserLogin {
        public String name;
        public String password;
    }

    private static class LoginResponse {
        public String token;

        public LoginResponse(final String token) {
            this.token = token;
        }
    }

    @GetMapping("/users")
    public List<User> getAllUsers() throws Exception {
        return userService.getAll();
    }
}
