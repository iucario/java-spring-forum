package com.demo.app.auth;

import com.demo.app.user.User;
import com.demo.app.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    UserRepository userRepository;
    private AuthService authService;
    private String password;
    private String hashedPassword;
    private User savedUser;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository);
        password = "testPass123!@#";
        hashedPassword = "$2b$12$C03E3lduNya6x8TG4WAEje8nrqHd2qaqn7rDbUn9SLCnjaA7.j/GC";
        savedUser = new User("testname", hashedPassword);
        savedUser.setId(1L);
    }

    @Test
    void canAuthenticate() {
        when(userRepository.findByName(any())).thenReturn(Optional.of(savedUser));
        Boolean actual = authService.authenticate("testname", password);
        verify(userRepository).findByName(any());
        assertEquals(true, actual);
    }
}
