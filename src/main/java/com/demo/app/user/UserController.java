package com.demo.app.user;

import com.demo.app.auth.JwtUtil;
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

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public LoginResponse login(@RequestBody final UserLogin login) throws Exception {
        System.out.printf("=> login: %s %s%n", login.name, login.password);
        if (!userService.authenticate(login.name, login.password)) {
            System.out.println("=> login: failed%n");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Invalid login. Please check your username and password.");
        }
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

    public static class UserCreate {
        public String name;
        public String password;

        public UserCreate(String name, String password) {
            this.name = name;
            this.password = password;
        }
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public String addNewUser(@RequestBody UserCreate user) throws Exception {
        userService.saveUser(user.name, user.password);
        return "Saved";
    }

    @GetMapping("/users")
    public List<User> getAllUsers() throws Exception {
        return userService.getAll();
    }
}
