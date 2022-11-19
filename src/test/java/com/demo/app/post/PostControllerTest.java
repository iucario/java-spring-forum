package com.demo.app.post;

import com.demo.app.auth.AuthService;
import com.demo.app.comment.CommentService;
import com.demo.app.user.User;
import com.demo.app.user.UserDto;
import com.demo.app.user.userStats.UserStats;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
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
    @MockBean
    CommentService commentService;
    private Post savedPost;
    private User savedUser;
    private UserStats userStats;
    private UserDto author;

    @BeforeEach
    void setUp() {
        savedUser = new User("testname", "Greatpassword1!");
        savedUser.setId(1L);
        savedPost = new Post("title", "content", savedUser);
        savedPost.setId(1L);
        userStats = new UserStats(savedUser);
        author = new UserDto(savedUser, userStats);
    }

    @Test
    void getUserPosts() throws Exception {
        List<PostDto> result = List.of(new PostDto(savedPost, author));
        when(postService.getUserPosts(1L, 0, 100)).thenReturn(result);
        mockMvc.perform(get("/api/post/user/1")
                        .param("offset", "0")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[0].title", Matchers.is("title")))
                .andExpect(jsonPath("$[0].body", Matchers.is("content")));
    }

    @Test
    void addNewPost() throws Exception {
        PostDto result = new PostDto(savedPost, author);
        PostDto.PostCreate postCreate = new PostDto.PostCreate("title", "content");
        when(postService.addPost(any(), any())).thenReturn(result);
        when(authService.getCurrentUser()).thenReturn(savedUser);
        mockMvc.perform(post("/api/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.title", Matchers.is("title")))
                .andExpect(jsonPath("$.body", Matchers.is("content")));
    }

    @Test
    void updatePost() throws Exception {
        PostDto result = new PostDto(savedPost, author);
        result.body = "body";
        PostDto.PostUpdate postUpdate = new PostDto.PostUpdate("update", 1L, "body");
        when(postService.getById(1L)).thenReturn(savedPost);
        when(postService.updatePost(any(), any())).thenReturn(result);
        when(authService.getCurrentUser()).thenReturn(savedUser);

        mockMvc.perform(put("/api/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(1)))
                .andExpect(jsonPath("$.title", Matchers.is("title")))
                .andExpect(jsonPath("$.body", Matchers.is("body")))
                .andExpect(jsonPath("$.author.id", Matchers.is(1)));
    }

    @Test
    void favoritePost() throws Exception {
        PostDto.PostUpdate postFav = new PostDto.PostUpdate("favorite", 1L, null);
        when(postService.getById(1L)).thenReturn(savedPost);
        when(authService.getCurrentUser()).thenReturn(savedUser);

        mockMvc.perform(put("/api/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postFav)))
                .andExpect(status().isOk());

        verify(postService).favoritePost(savedUser, 1L);
    }

    @Test
    void unfavoritePost() throws Exception {
        PostDto.PostUpdate postFav = new PostDto.PostUpdate("unfavorite", 1L, null);
        when(postService.getById(1L)).thenReturn(savedPost);
        when(authService.getCurrentUser()).thenReturn(savedUser);

        mockMvc.perform(put("/api/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postFav)))
                .andExpect(status().isOk());

        verify(postService).unfavoritePost(savedUser, 1L);
    }

    @Test
    void deletePost() throws Exception {
        when(authService.getCurrentUser()).thenReturn(savedUser);
        when(postService.getById(1L)).thenReturn(savedPost);
        mockMvc.perform(delete("/api/post/" + savedPost.getId()))
                .andExpect(status().is2xxSuccessful());
    }
}