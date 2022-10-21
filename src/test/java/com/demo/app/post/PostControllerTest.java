package com.demo.app.post;

import com.demo.app.auth.AuthService;
import com.demo.app.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(PostController.class)
class PostControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    AuthService authService;
    @MockBean
    PostService postService;
    private Post savedPost;
    private User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = new User("testname", "Greatpassword1!");
        savedUser.setId(1L);
        savedPost = new Post("title", "content", savedUser);
        savedPost.setId(1L);
    }

    @Test
    void canGetAllPosts() throws Exception {
        List<PostDto> result = List.of(new PostDto(savedPost));
        when(postService.getUserPosts(1L, 0, 100)).thenReturn(result);
        when(authService.getUser(any())).thenReturn(savedUser);
        mockMvc.perform(get("/api/post")
                        .requestAttr("claims", "{\"sub\":\"testname\"}")
                        .param("offset", "0")
                        .param("limit", "100"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title", Matchers.is("title")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].body", Matchers.is("content")));
    }

    @Test
    void canAddNewPost() throws Exception {
        PostDto result = new PostDto(savedPost);
        PostDto.PostCreate postCreate = new PostDto.PostCreate("title", "content");
        when(postService.addPost(any())).thenReturn(result);
        when(authService.getUser(any())).thenReturn(savedUser);
        mockMvc.perform(post("/api/post")
                        .requestAttr("claims", "{\"sub\":\"testname\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postCreate)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("title")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body", Matchers.is("content")));
    }

    @Test
    void canUpdatePost() throws Exception {
        PostDto result = new PostDto(savedPost);
        result.body = "body";
        PostDto.PostUpdate postUpdate = new PostDto.PostUpdate("body", 1L);
        when(postService.getById(1L)).thenReturn(savedPost);
        when(postService.updatePost(any())).thenReturn(result);
        when(authService.getUser(any())).thenReturn(savedUser);
        mockMvc.perform(put("/api/post")
                        .requestAttr("claims", "{\"sub\":\"testname\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postUpdate)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title", Matchers.is("title")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.body", Matchers.is("body")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", Matchers.is(1)));
    }

    @Test
    void canDeletePost() throws Exception {
        when(authService.getUser(any())).thenReturn(savedUser);
        when(postService.getById(1L)).thenReturn(savedPost);
        mockMvc.perform(delete("/api/post/" + savedPost.getId())
                        .requestAttr("claims", "{\"sub\":\"testname\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Deleted 1"));
    }

    @Test
    void willReturn401() throws Exception {
        User otherUser = new User("othername", "Greatpassword1!");
        otherUser.setId(2L);
        when(authService.getUser(any())).thenReturn(otherUser);
        when(postService.getById(1L)).thenReturn(savedPost);
        mockMvc.perform(delete("/api/post/" + savedPost.getId())
                        .requestAttr("claims", "{\"sub\":\"othername\"}"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        mockMvc.perform(put("/api/post")
                        .requestAttr("claims", "{\"sub\":\"othername\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PostDto.PostUpdate("new body", 1L))))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}