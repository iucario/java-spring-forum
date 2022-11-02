package com.demo.app.user;

import com.demo.app.auth.AuthService;
import com.demo.app.auth.JwtUtil;
import com.demo.app.exception.AppException;
import com.demo.app.exception.AppException.UserExistsException;
import com.demo.app.post.PostRepository;
import com.demo.app.user.userStats.UserStats;
import com.demo.app.user.userStats.UserStatsRepository;
import com.demo.app.user.userStats.UserStatsService;
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
    UserStatsRepository userStatsRepository;
    @Mock
    AuthService authService;
    @Mock
    UserStatsService userStatsService;
    private JwtUtil jwtUtil;
    private String password;
    private String hashedPassword;
    private UserService userService;
    private User savedUser;
    private UserStats userStats;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("secret123456789012345678901234567890");
        password = "testPass123!@#";
        hashedPassword = "$2b$12$C03E3lduNya6x8TG4WAEje8nrqHd2qaqn7rDbUn9SLCnjaA7.j/GC";
        userService = new UserService(userRepository, userStatsService, authService, jwtUtil);
        savedUser = new User("testname", hashedPassword);
        savedUser.setId(1L);
        userStats = new UserStats(savedUser);
    }

    @Test
    void getAll() {
        userService.getAll();
        verify(userRepository).findAll();
    }

    @Test
    void canCreateUser() {
        UserDto.UserCreate userCreate = new UserDto.UserCreate("testname", password);
        when(userRepository.findByName(any())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userStatsService.save(any(UserStats.class))).thenReturn(userStats);
        userService.createUser(userCreate);
        verify(userRepository).save(any());
    }

    @Test
    void willThrowException_WhenUserExists() {
        UserDto.UserCreate userCreate = new UserDto.UserCreate("testname", password);
        when(userRepository.findByName(any())).thenReturn(Optional.of(savedUser));
        assertThrows(UserExistsException.class, () -> userService.createUser(userCreate));
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
    void willThrowNotFoundException_WhenUserNotFound() {
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
        when(userStatsService.getUserStats(savedUser.getId())).thenReturn(userStats);
        userService.getUserInfo(savedUser);
        verify(userStatsService).getUserStats(savedUser.getId());
    }

    @Test
    void canGetUserProfile() {
        when(userRepository.findById(savedUser.getId())).thenReturn(Optional.of(savedUser));
        when(userStatsService.getUserStats(savedUser.getId())).thenReturn(userStats);
        userService.getUserProfile(savedUser.getId());
        verify(userStatsService).getUserStats(savedUser.getId());
        verify(userRepository).findById(savedUser.getId());
    }

    @Test
    void canDeleteUserById() {
        userService.deleteUserById(savedUser.getId());
        verify(userRepository).deleteById(savedUser.getId());
    }
}