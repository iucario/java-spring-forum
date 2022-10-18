package com.demo.app.user;

import com.demo.app.post.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PostRepository postRepository;
    private UserService userService;
    private User savedUser;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, postRepository);
        savedUser = new User("testname", "testpassword");
    }

    @Test
    void getAll() {
        userService.getAll();
        verify(userRepository).findAll();
    }

    @Test
    void canRegister() {
        UserDto.UserCreate userCreate = new UserDto.UserCreate("testname", "testPass123!@#");
        ResponseEntity<String> resp = userService.register(userCreate);
        verify(userRepository).save(any());
        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals("User created", resp.getBody());
    }

    @Test
    void canGetByName() {
        when(userRepository.findByName(savedUser.getName())).thenReturn(Optional.of(savedUser));
        userService.getByName("testname");
        verify(userRepository).findByName("testname");
    }

    @Test
    void canGetById() {
        when(userRepository.findById(savedUser.getId())).thenReturn(Optional.of(savedUser));
        userService.getById(savedUser.getId());
        verify(userRepository).findById(savedUser.getId());
    }

    @Test
    void canSaveUser() {
        userService.saveUser("testname", "testPass123!@#");
        verify(userRepository).save(any());
    }

}