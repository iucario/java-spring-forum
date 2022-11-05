package com.demo.app.user;

import com.demo.app.auth.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public UserDto.LoginResponse login(@RequestBody final UserDto.UserLogin login) {
        return userService.login(login);
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserDto.UserCreate user) {
        UserDto userDto = userService.createUser(user);
        return new ResponseEntity<>(userDto, null, HttpStatus.CREATED);
    }

    @GetMapping(value = "/me", produces = "application/json")
    public UserDto getMe() {
        User user = authService.getCurrentUser();
        System.out.println(user);
        return userService.getUserInfo(user);
    }

    @GetMapping(value = "/profile/{id}", produces = "application/json")
    public UserDto getUserProfile(@PathVariable Long id) {
        return userService.getUserProfile(id);
    }
}
