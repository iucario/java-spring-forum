package com.demo.app.user;

import com.demo.app.auth.AuthService;
import com.demo.app.post.Post;
import com.demo.app.post.PostService;
import com.demo.app.user.userStats.UserStats;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    AuthService authService;
    @MockBean
    UserService userService;
    @MockBean
    PostService postService;
    @Autowired
    ObjectMapper objectMapper;
    private User savedUser;
    private UserStats userStats;
    private Post savedPost;

    @BeforeEach
    void setUp() {
        savedUser = new User("testname", "testpassword");
        savedUser.setId(1L);
        userStats = new UserStats(savedUser);
        savedUser.setUserStats(userStats);
        savedPost = new Post("title", "post body", savedUser);
        savedPost.setId(1L);
    }

    @Test
    void login() throws Exception {
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
    void register() throws Exception {
        UserDto.UserCreate userCreate = new UserDto.UserCreate("testname", "Greatpassword1!");
        UserDto result = new UserDto(savedUser, userStats);
        when(userService.createUser(userCreate)).thenReturn(result);
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
    void getMe() throws Exception {
        UserDto result = new UserDto(savedUser, userStats);
        when(userService.getUserInfo(any())).thenReturn(result);
        mockMvc.perform(get("/user/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is("testname")))
                .andExpect(jsonPath("$.postCount", Matchers.is(0)));
    }

    @Test
    void getUserProfile() throws Exception {
        UserDto result = new UserDto(savedUser, userStats);
        when(userService.getUserProfile(any())).thenReturn(result);
        mockMvc.perform(get("/user/profile/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(result)));
    }

    @Test
    void getUserFavorites() throws Exception {
        UserDto author = new UserDto(savedUser, userStats);
        when(userService.getUserFavorites(any())).thenReturn(List.of(savedPost));
        mockMvc.perform(get("/user/1/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", Matchers.is("title")));
    }
}