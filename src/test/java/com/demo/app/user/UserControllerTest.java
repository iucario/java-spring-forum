package com.demo.app.user;

import com.demo.app.auth.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    AuthService authService;
    @MockBean
    UserService userService;
    @Autowired
    ObjectMapper objectMapper;
    private User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = new User("testname", "testpassword");
        savedUser.setId(1L);
    }

    @Test
    void canLogin() throws Exception {
        UserDto.UserLogin userLogin = new UserDto.UserLogin("testname", "Greatpassword1!");
        UserDto.LoginResponse result = new UserDto.LoginResponse("token");
        when(userService.login(any(UserDto.UserLogin.class))).thenReturn(result);
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLogin)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(result)));
    }

    @Test
    void willReturn401() throws Exception {
        UserDto.UserLogin userLogin = new UserDto.UserLogin("testname", "Greatpassword1!");
        ResponseStatusException result = new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or " +
                "password");
        when(userService.login(any())).thenThrow(result);
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLogin)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void canRegister() throws Exception {
        UserDto.UserCreate userCreate = new UserDto.UserCreate("testname", "Greatpassword1!");
        UserDto result = new UserDto(savedUser, 0);
        when(userService.register(userCreate)).thenReturn(result);
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreate)))
                .andExpect(status().isCreated());
    }

    @Test
    void willReturn422() throws Exception {
        UserDto.UserCreate userCreate = new UserDto.UserCreate("testname", "badpassword");
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreate)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void canGetMe() throws Exception {
        UserDto result = new UserDto(savedUser, 1);
        when(userService.getUserInfo(any())).thenReturn(result);
        mockMvc.perform(get("/user/me")
                        .requestAttr("claims", "{\"sub\":\"testname\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is("testname")))
                .andExpect(jsonPath("$.totalPosts", Matchers.is(1)));
    }

    @Test
    void canGetUserProfile() throws Exception {
        UserDto result = new UserDto(savedUser, 1);
        when(userService.getUserProfile(any())).thenReturn(result);
        mockMvc.perform(get("/user/profile/1")
                        .requestAttr("claims", "{\"sub\":\"testname\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(result)));
    }
}