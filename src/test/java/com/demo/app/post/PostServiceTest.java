package com.demo.app.post;

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

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    PostRepository postRepository;
    private PostService postService;
    private UserService userService;
    private User savedUser;
    private Post savedPost;

    @BeforeEach
    void setUp() {
        postService = new PostService(postRepository);
        userService = new UserService(userRepository, postRepository);
        savedUser = new User("testname", "testpassword");
        savedPost = new Post("title", "This is body", savedUser);
    }

    @Test
    void canAddPost() {
        when(postRepository.save(any(Post.class))).thenReturn(savedPost);
        Post post = postService.addPost(savedPost);
        verify(postRepository).save(any(Post.class));
        assertEquals(savedPost, post);
    }

    @Test
    void canGetById() {
        when(postRepository.findById(savedPost.getId())).thenReturn(Optional.of(savedPost));
        postService.getById(savedPost.getId());
        verify(postRepository).findById(savedPost.getId());
    }

    @Test
    void canGetAll() {
        when(postRepository.getAll(savedUser.getId(), 0, 100)).thenReturn(List.of(savedPost));
        postService.addPost(savedPost);
        List<Post> posts = postService.getAllPosts(savedUser.getId(), 0, 100);
        verify(postRepository).getAll(savedUser.getId(), 0, 100);
        assertEquals(1, posts.size());
        assertEquals(savedPost, posts.get(0));
    }

    @Test
    void canDeletePost() {
        postService.deletePostById(savedPost.getId());
        verify(postRepository).deleteById(savedPost.getId());
    }

    @Test
    void canUpdatePost() {
        PostDto.PostUpdate postUpdate = new PostDto.PostUpdate("updated body", savedPost.getId());
        when(postRepository.findById(savedPost.getId())).thenReturn(Optional.ofNullable(savedPost));
        savedPost.setBody(postUpdate.body);
        when(postRepository.save(any(Post.class))).thenReturn(savedPost);
        PostDto updatedPost = postService.updatePost(postUpdate);
        assertEquals("updated body", updatedPost.body);
    }
}
