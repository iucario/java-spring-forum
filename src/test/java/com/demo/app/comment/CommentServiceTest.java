package com.demo.app.comment;

import com.demo.app.post.Post;
import com.demo.app.post.PostRepository;
import com.demo.app.post.PostService;
import com.demo.app.user.User;
import com.demo.app.user.UserRepository;
import com.demo.app.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private CommentService commentService;
    private User savedUser;
    private Post savedPost;
    private Comment savedComment;

    @BeforeEach
    void setUp() {
        UserService userService = new UserService(userRepository, postRepository);
        PostService postService = new PostService(postRepository);
        commentService = new CommentService(commentRepository, postService);
        savedUser = new User("testname", "testpassword");
        savedPost = new Post("title", "This is body", savedUser);
        savedComment = new Comment("This is comment", savedPost, savedUser);
    }

    @Test
    void canAddComment() {
        CommentDto.CommentCreate commentCreate = new CommentDto.CommentCreate("This is comment", savedPost.getId());
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);
        when(postRepository.findById(savedPost.getId())).thenReturn(Optional.of(savedPost));
        Comment comment = commentService.addComment(commentCreate, savedUser);
        verify(commentRepository).save(any(Comment.class));
        assertEquals(savedComment, comment);
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

}
