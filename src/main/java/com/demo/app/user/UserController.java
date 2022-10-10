package com.demo.app.user;

import com.demo.app.auth.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public UserDto.LoginResponse login(@RequestBody final UserDto.UserLogin login) throws Exception {
        if (!userService.authenticate(login.name, login.password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Invalid login. Please check your username and password.");
        }
        return new UserDto.LoginResponse(jwtUtil.generateToken(login.name));
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> register(@Valid @RequestBody UserDto.UserCreate user) throws Exception {
        if (userService.getByName(user.name) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        }
        userService.saveUser(user.name, user.password);
        return ResponseEntity.status(HttpStatus.CREATED).body("User created");
    }

    @GetMapping(value = "/me", produces = "application/json")
    public UserDto getMe(final HttpServletRequest request) throws Exception {
        final Claims claims = (Claims) request.getAttribute("claims");
        final User user = userService.getByName(claims.get("sub", String.class));
        int totalItems = userService.countItems(user.getId());
        return new UserDto(user.getName(), user.getCreatedAt(), totalItems);
    }
}
