package com.demo.app.post;

import com.demo.app.common.RedisUtil;
import com.demo.app.exception.AppException;
import com.demo.app.favorite.FavUserPostRepository;
import com.demo.app.user.User;
import com.demo.app.user.UserDto;
import com.demo.app.user.UserService;
import com.demo.app.user.userStats.UserStats;
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
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    PostRepository postRepository;
    @Mock
    UserService userService;
    @Mock
    FavUserPostRepository favUserPostRepository;
    @Mock
    ZSetOperations<String, Object> zSetOperations;
    @Mock
    ValueOperations<String, Object> valueOperations;
    Logger logger = Logger.getLogger(PostServiceTest.class.getName());
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private RedisUtil redisUtil;
    private PostService postService;
    private User savedUser;
    private Post savedPost;

    @BeforeEach
    void setUp() {
        postService = new PostService(postRepository, userService, favUserPostRepository, redisUtil);
        savedUser = new User("testname", "testpassword");
        savedUser.setId(1L);
        savedUser.setUserStats(new UserStats(savedUser));
        savedPost = new Post("title", "This is body", savedUser);
        savedPost.setId(1L);
        savedPost.setComments(List.of());
        Mockito.lenient().when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        Mockito.lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void addPost() {
        when(postRepository.save(any(Post.class))).thenReturn(savedPost);
        savedUser.setUserStats(1L, 0L, 0L);
        UserDto expected = new UserDto(savedUser);
        when(userService.saveUserStats(any(UserStats.class))).thenReturn(expected);

        postService.addPost(savedPost, savedUser);

        verify(postRepository).save(any(Post.class));
        verify(userService).saveUserStats(any(UserStats.class));
    }

    @Test
    void getById() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(savedPost));
        postService.getById(savedPost.getId());
        verify(postRepository).findById(savedPost.getId());
    }

    @Test
    void willThrowPostNotFoundException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        Throwable exception = assertThrows(AppException.PostNotFoundException.class, () -> {
            postService.getById(savedPost.getId());
        });
        assertEquals("Post not found for id : 1", exception.getMessage());
        verify(postRepository).findById(savedPost.getId());
    }

    @Test
    void getUserPosts() {
        when(postRepository.findUserPosts(1L, 0, 100)).thenReturn(List.of(savedPost));
        List<PostDto> posts = postService.getUserPosts(1L, 0, 100);
        verify(postRepository).findUserPosts(1L, 0, 100);
        assertEquals(1, posts.size());
        assertEquals(savedPost.getBody(), posts.get(0).body);
    }

    @Test
    void deletePostById() {
        Long expected = savedUser.getUserStats().getPostCount() - 1;
        when(postRepository.findById(savedPost.getId())).thenReturn(Optional.of(savedPost));

        postService.deletePostById(savedPost.getId(), savedUser);

        verify(postRepository).findById(savedPost.getId());
        verify(postRepository).deleteById(savedPost.getId());
        assertEquals(expected, savedUser.getUserStats().getPostCount());
    }

    @Test
    void will_throw_unauthorized_exception_when_post_user_not_match() {
        User user = new User("testname", "testpassword");
        user.setId(2L);
        when(postRepository.findById(savedPost.getId())).thenReturn(Optional.of(savedPost));
        Throwable exception = assertThrows(AppException.UnauthorizedException.class, () -> {
            postService.deletePostById(savedPost.getId(), user);
        });
        assertEquals("Unauthorized", exception.getMessage());
        verify(postRepository).findById(savedPost.getId());
    }

    @Test
    void updatePost() {
        PostDto.PostUpdate postUpdate = new PostDto.PostUpdate("update", savedPost.getId(), "updated body");
        savedPost.setBody(postUpdate.body);
        when(postRepository.findById(savedPost.getId())).thenReturn(Optional.ofNullable(savedPost));
        when(postRepository.save(any(Post.class))).thenReturn(savedPost);

        PostDto actual = postService.updatePost(postUpdate, savedUser);

        assertEquals("updated body", actual.body);
    }

    @Test
    void getPostList() {
        when(postRepository.findPosts(eq(0), anyInt())).thenReturn(List.of(savedPost));

        List<PostDto> postList = postService.getPostList(0, 10);

        verify(postRepository).findPosts(0, 10);
    }

    @Test
    void favoritePost() {
        when(postRepository.findById(savedPost.getId())).thenReturn(Optional.of(savedPost));
        when(favUserPostRepository.save(any())).thenReturn(null);

        postService.favoritePost(savedUser, savedPost.getId());

        verify(postRepository).findById(savedPost.getId());
        verify(favUserPostRepository).save(any());
    }
}
