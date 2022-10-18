package com.demo.app.user;

import com.demo.app.auth.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    UserService userService;
    @MockBean
    JwtUtil jwtUtil;
    @Autowired
    ObjectMapper objectMapper;
    private User savedUser;

    @BeforeEach
    void setUp() {
    }

    @Test
    void canLogin() throws Exception {
        UserDto.UserLogin userLogin = new UserDto.UserLogin("testname", "Greatpassword1!");
        UserDto.LoginResponse result = new UserDto.LoginResponse("token");
        when(userService.authenticate(userLogin.name, userLogin.password)).thenReturn(true);
        when(jwtUtil.generateToken(userLogin.name)).thenReturn("token");
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLogin)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token", Matchers.is("token")));
    }

    @Test
    void willReturn401() throws Exception {
        UserDto.UserLogin userLogin = new UserDto.UserLogin("testname", "Greatpassword1!");
        when(userService.authenticate(userLogin.name, userLogin.password)).thenReturn(false);
        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLogin)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void canRegister() throws Exception {
        UserDto.UserCreate userCreate = new UserDto.UserCreate("testname", "Greatpassword1!");
        UserDto result = new UserDto("testname", new Date().getTime(), 0);
        ResponseEntity<String> resp = ResponseEntity.status(HttpStatus.CREATED).body("User created");
        when(userService.register(userCreate)).thenReturn(resp);
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreate)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void willReturn422() throws Exception {
        UserDto.UserCreate userCreate = new UserDto.UserCreate("testname", "badpassword");
        UserDto result = new UserDto("testname", new Date().getTime(), 0);
        ResponseEntity<String> resp = ResponseEntity.status(HttpStatus.CREATED).body("User created");
        when(userService.register(userCreate)).thenReturn(resp);
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreate)))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    void canGetMe() throws Exception {
        UserDto result = new UserDto("testname", new Date().getTime(), 0);
        when(userService.getUserInfo(any())).thenReturn(result);
        mockMvc.perform(get("/user/me")
                        .requestAttr("claims", "{\"sub\":\"testname\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is("testname")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPosts", Matchers.is(0)));
    }
}