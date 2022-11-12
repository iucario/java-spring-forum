package com.demo.app.post;

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
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    PostRepository postRepository;
    @Mock
    UserService userService;
    @Mock
    FavUserPostRepository favUserPostRepository;
    @Mock
    ListOperations<String, Object> listOperations;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    private PostService postService;
    private User savedUser;
    private Post savedPost;

    @BeforeEach
    void setUp() {
        postService = new PostService(postRepository, userService, favUserPostRepository, redisTemplate);
        savedUser = new User("testname", "testpassword");
        savedUser.setId(1L);
        savedUser.setUserStats(new UserStats(savedUser));
        savedPost = new Post("title", "This is body", savedUser);
        savedPost.setId(1L);
        Mockito.lenient().when(redisTemplate.opsForList()).thenReturn(listOperations);
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
        when(postRepository.findPosts(0, 10)).thenReturn(List.of(savedPost));

        List<PostDto.PostListDto> postList = postService.getPostList(0, 10);

        verify(postRepository).findPosts(0, 10);
    }

    @Test
    void get_post_list_from_cache_when_has_key() {
        when(redisTemplate.hasKey("postList")).thenReturn(true);

        List<PostDto.PostListDto> postList = postService.getPostList(0, 10);

        verify(listOperations).range("postList", 0, 9);
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
