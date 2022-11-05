package com.demo.app.comment;

import com.demo.app.auth.AuthService;
import com.demo.app.post.Post;
import com.demo.app.post.PostService;
import com.demo.app.user.User;
import com.demo.app.user.UserDto;
import com.demo.app.user.userStats.UserStats;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(CommentController.class)
class CommentControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    CommentService commentService;
    @MockBean
    AuthService authService;
    @MockBean
    PostService postService;
    private Comment savedComment;
    private Post savedPost;
    private User savedUser;
    private UserDto author;

    @BeforeEach
    void setUp() {
        savedUser = new User("testname", "Greatpassword1!");
        savedUser.setId(1L);
        savedPost = new Post("title", "content", savedUser);
        savedPost.setId(1L);
        savedComment = new Comment("content", savedPost, savedUser);
        savedComment.setId(1L);
        author = new UserDto(savedUser, new UserStats(savedUser));
    }

    @Test
    void canGetComments() throws Exception {
        List<CommentDto> result = List.of(new CommentDto(savedComment, author));
        when(postService.getById(1L)).thenReturn(savedPost);
        when(commentService.getByPostId(1L, 0, 20)).thenReturn(result);
        mockMvc.perform(get("/api/comment?postId=1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)));

        when(commentService.getByPostAndUser(1L, 1L)).thenReturn(result);
        mockMvc.perform(get("/api/comment?postId=1&userId=1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)));
    }

    @Test
    void canAddComment() throws Exception {
        CommentDto result = new CommentDto(savedComment, author);
        when(authService.getCurrentUser()).thenReturn(savedUser);
        when(postService.getById(1L)).thenReturn(savedPost);
        when(commentService.addComment(any(), eq(savedUser))).thenReturn(result);
        mockMvc.perform(post("/api/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"body\":\"content\",\"postId\":1}"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)));
    }

    @Test
    void canDeleteComment() throws Exception {
        when(authService.getCurrentUser()).thenReturn(savedUser);
        mockMvc.perform(delete("/api/comment/1"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void canUpdateComment() throws Exception {
        CommentDto result = new ResponseEntity<>(new CommentDto(savedComment, author), null, 200).getBody();
        when(authService.getCurrentUser()).thenReturn(savedUser);
        when(commentService.updateComment(any(), eq(savedUser))).thenReturn(result);
        mockMvc.perform(put("/api/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"body\":\"content\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)));
    }
}