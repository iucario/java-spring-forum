package com.demo.app.comment;

import com.demo.app.auth.AuthService;
import com.demo.app.auth.JwtUtil;
import com.demo.app.post.Post;
import com.demo.app.post.PostRepository;
import com.demo.app.post.PostService;
import com.demo.app.user.User;
import com.demo.app.user.UserRepository;
import com.demo.app.user.UserService;
import com.demo.app.user.userStats.UserStatsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    CommentRepository commentRepository;
    @Mock
    PostRepository postRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    UserStatsRepository userStatsRepository;
    @Mock
    AuthService authService;
    @Autowired
    JwtUtil jwtUtil;
    private CommentService commentService;
    private User savedUser;
    private Post savedPost;
    private Comment savedComment;

    @BeforeEach
    void setUp() {
        UserService userService = new UserService(userRepository, postRepository, userStatsRepository, authService,
                jwtUtil);
        PostService postService = new PostService(postRepository);
        commentService = new CommentService(commentRepository, postService);
        savedUser = new User("testname", "testpassword");
        savedUser.setId(1L);
        savedPost = new Post("title", "This is body", savedUser);
        savedPost.setId(1L);
        savedComment = new Comment("This is comment", savedPost, savedUser);
        savedComment.setId(1L);
    }

    @AfterEach
    void tearDown() {
        commentRepository.deleteAll();
    }

    @Test
    void canAddComment() {
        CommentDto.CommentCreate commentCreate = new CommentDto.CommentCreate("This is comment", savedPost.getId());
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);
        when(postRepository.findById(savedPost.getId())).thenReturn(Optional.of(savedPost));
        CommentDto comment = commentService.addComment(commentCreate, savedUser);
        verify(commentRepository).save(any(Comment.class));
        assertEquals(commentCreate.body, comment.body);
    }

    @Test
    void canGetById() {
        when(commentRepository.findById(savedComment.getId())).thenReturn(Optional.of(savedComment));
        Comment comment = commentService.getById(savedComment.getId());
        verify(commentRepository).findById(savedComment.getId());
        assertEquals(savedComment, comment);
        assertEquals(savedPost, comment.getPost());
        assertEquals(savedUser, comment.getUser());
    }

    @Test
    void canGetByPostId() {
        when(commentRepository.findByPostId(savedPost.getId())).thenReturn(List.of(savedComment));
        commentService.getByPostId(savedPost.getId());
        verify(commentRepository).findByPostId(savedPost.getId());
    }

    @Test
    void canGetByPostAndUser() {
        when(commentRepository.findByPostAndUser(savedPost.getId(), savedUser.getId())).thenReturn(List.of(savedComment));
        commentService.getByPostAndUser(savedPost.getId(), savedUser.getId());
        verify(commentRepository).findByPostAndUser(savedPost.getId(), savedUser.getId());
    }

    @Test
    void canDeleteComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(savedComment));
        commentService.deleteComment(1L, savedUser);
        verify(commentRepository).deleteById(1L);
    }

    @Test
    void canUpdateComment() {
        CommentDto.CommentUpdate commentUpdate = new CommentDto.CommentUpdate(1L, "This is updated comment");
        when(commentRepository.findById(1L)).thenReturn(Optional.of(savedComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);
        commentService.updateComment(commentUpdate, savedUser);
        verify(commentRepository).save(any(Comment.class));
    }

}
