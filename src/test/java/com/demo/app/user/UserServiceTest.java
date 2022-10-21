package com.demo.app.user;

import com.demo.app.auth.AuthService;
import com.demo.app.auth.JwtUtil;
import com.demo.app.exception.AppException;
import com.demo.app.post.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.jgroups.util.Util.assertEquals;
import static org.jgroups.util.Util.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    AuthService authService;
    private JwtUtil jwtUtil;
    private String password;
    private String hashedPassword;
    private UserService userService;
    private User savedUser;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("secret123456789012345678901234567890");
        password = "testPass123!@#";
        hashedPassword = "$2b$12$C03E3lduNya6x8TG4WAEje8nrqHd2qaqn7rDbUn9SLCnjaA7.j/GC";
        userService = new UserService(userRepository, postRepository, authService, jwtUtil);
        savedUser = new User("testname", hashedPassword);
        savedUser.setId(1L);
    }

    @Test
    void getAll() {
        userService.getAll();
        verify(userRepository).findAll();
    }

    @Test
    void canRegister() {
        UserDto.UserCreate userCreate = new UserDto.UserCreate("testname", password);
        when(userRepository.findByName(any())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        userService.register(userCreate);
        verify(userRepository).save(any());
    }

    @Test
    void canLogin() {
        UserDto.UserLogin userLogin = new UserDto.UserLogin("testname", password);
        when(authService.authenticate("testname", password)).thenReturn(true);
        UserDto.LoginResponse result = userService.login(userLogin);
        assertNotNull(result);
    }

    @Test
    void canGetByName() {
        when(userRepository.findByName(savedUser.getName())).thenReturn(Optional.of(savedUser));
        userService.getByName("testname");
        verify(userRepository).findByName("testname");
    }

    @Test
    void willReturnUserNotFoundException() {
        when(userRepository.findByName(savedUser.getName())).thenReturn(Optional.empty());
        var expected = new AppException.NotFoundException("User not found for name: testname");
        Throwable exception = assertThrows(AppException.NotFoundException.class,
                () -> userService.getByName("testname"));
        assertEquals(expected.getMessage(), exception.getMessage());
        verify(userRepository).findByName("testname");
    }

    @Test
    void canGetById() {
        when(userRepository.findById(savedUser.getId())).thenReturn(Optional.of(savedUser));
        userService.getById(savedUser.getId());
        verify(userRepository).findById(savedUser.getId());
    }

    @Test
    void canGetUserInfo() {
        when(postRepository.countUserPosts(savedUser.getId())).thenReturn(1);
        userService.getUserInfo(savedUser);
        verify(postRepository).countUserPosts(savedUser.getId());
    }

    @Test
    void canGetUserProfile() {
        when(userRepository.findById(savedUser.getId())).thenReturn(Optional.of(savedUser));
        userService.getUserProfile(savedUser.getId());
        verify(userRepository).findById(savedUser.getId());
    }

    @Test
    void canDeleteUserById() {
        userService.deleteUserById(savedUser.getId());
        verify(userRepository).deleteById(savedUser.getId());
    }
}