package com.demo.app.comment;

import com.demo.app.common.RedisUtil;
import com.demo.app.post.Post;
import com.demo.app.post.PostService;
import com.demo.app.user.User;
import com.demo.app.user.UserDto;
import com.demo.app.user.UserService;
import com.demo.app.user.userStats.UserStats;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

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
    UserService userService;
    @Mock
    PostService postService;
    @Mock
    ZSetOperations<String, Object> zSetOperations;
    @Mock
    ValueOperations<String, Object> valueOperations;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private RedisUtil redisUtil;
    private CommentService commentService;
    private User savedUser;
    private Post savedPost;
    private Comment savedComment;
    private UserDto savedAuthor;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(commentRepository, postService, userService, redisUtil);
        savedUser = new User("testname", "testpassword");
        savedUser.setId(1L);
        savedUser.setUserStats(new UserStats(savedUser));
        savedPost = new Post("title", "This is body", savedUser);
        savedPost.setId(1L);
        savedComment = new Comment("This is comment", savedPost, savedUser);
        savedComment.setId(1L);
        savedAuthor = new UserDto(savedUser);
        Mockito.lenient().when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        Mockito.lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @AfterEach
    void tearDown() {
        commentRepository.deleteAll();
    }

    @Test
    void getCommentAuthor() {
        when(commentRepository.findById(savedComment.getId())).thenReturn(Optional.of(savedComment));
        UserDto user = commentService.getCommentAuthor(savedComment.getId());
        verify(commentRepository).findById(savedComment.getId());
        assertEquals(savedUser.getName(), user.name);
    }

    @Test
    void addComment() {
        CommentDto.CommentCreate commentCreate = new CommentDto.CommentCreate("This is comment", savedPost.getId());
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);
        when(postService.getById(savedPost.getId())).thenReturn(savedPost);

        CommentDto comment = commentService.addComment(commentCreate, savedUser);

        verify(commentRepository).save(any(Comment.class));
        verify(userService).saveUserStats(any(UserStats.class));
        assertEquals(commentCreate.body, comment.body);
    }

    @Test
    void getById() {
        when(commentRepository.findById(savedComment.getId())).thenReturn(Optional.of(savedComment));
        Comment comment = commentService.getById(savedComment.getId());
        verify(commentRepository).findById(savedComment.getId());
        assertEquals(savedComment, comment);
        assertEquals(savedPost, comment.getPost());
        assertEquals(savedUser, comment.getUser());
    }

    @Test
    void getByPostId() {
        when(commentRepository.findByPostId(savedPost.getId(), 0, 100)).thenReturn(List.of(savedComment));

        commentService.getByPostId(savedPost.getId(), 0, 100);

        verify(commentRepository).findByPostId(savedPost.getId(), 0, 100);
    }

    @Test
    void getByPostAndUser() {
        when(userService.getUserProfile(savedUser.getId())).thenReturn(savedAuthor);
        when(commentRepository.findByPostAndUser(savedPost.getId(), savedUser.getId())).thenReturn(List.of(savedComment));
        commentService.getByPostAndUser(savedPost.getId(), savedUser.getId());
        verify(commentRepository).findByPostAndUser(savedPost.getId(), savedUser.getId());
    }

    @Test
    void deleteComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(savedComment));
        commentService.deleteComment(1L, savedUser);
        verify(commentRepository).deleteById(1L);
        verify(userService).saveUserStats(any(UserStats.class));
    }

    @Test
    void updateComment() {
        CommentDto.CommentUpdate commentUpdate = new CommentDto.CommentUpdate(1L, "This is updated comment");
        when(commentRepository.findById(1L)).thenReturn(Optional.of(savedComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);
        commentService.updateComment(commentUpdate, savedUser);
        verify(commentRepository).save(any(Comment.class));
    }

}
