package com.demo.app.user;

import com.demo.app.auth.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public UserDto.LoginResponse login(@RequestBody final UserDto.UserLogin login) {
        if (!userService.authenticate(login.name, login.password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
        return new UserDto.LoginResponse(jwtUtil.generateToken(login.name));
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> register(@Valid @RequestBody UserDto.UserCreate user) {
        return userService.register(user);
    }

    @GetMapping(value = "/me", produces = "application/json")
    public UserDto getMe(final HttpServletRequest request) {
        return userService.getUserInfo(request);
    }
}
