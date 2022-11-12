package com.demo.app.post;

import com.demo.app.exception.AppException;
import com.demo.app.favorite.FavUserPost;
import com.demo.app.favorite.FavUserPostRepository;
import com.demo.app.user.User;
import com.demo.app.user.UserDto;
import com.demo.app.user.UserService;
import org.slf4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class PostService {
    final Logger logger = org.slf4j.LoggerFactory.getLogger(PostService.class);
    private final PostRepository postRepository;
    private final UserService userService;
    private final FavUserPostRepository favUserPostRepository;
    private RedisTemplate<String, Object> redisTemplate;

    public PostService(PostRepository postRepository, UserService userService,
                       FavUserPostRepository favUserPostRepository, RedisTemplate<String, Object> redisTemplate) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.favUserPostRepository = favUserPostRepository;
        this.redisTemplate = redisTemplate;
    }

    public List<PostDto.PostListDto> getPostList(final int offset, final int limit) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey("postList"))) {
            List<Object> posts = redisTemplate.opsForList().range("postList", offset, offset + limit - 1);
            logger.info("Key 'postList' found in cache");
            logger.info("Posts %s".formatted(posts));
            return posts.stream().map(PostDto.PostListDto.class::cast).toList();
        } else {
            List<PostDto.PostListDto> postList = postRepository.findPosts(offset, limit)
                    .stream()
                    .map(this::createPostListDto)
                    .toList();
            logger.info("Initialize key 'postList' to the cache");
            redisTemplate.opsForList().rightPushAll("postList", postList.toArray());
            return postList;
        }
    }

    public List<PostDto> getUserPosts(Long userId, int offset, int limit) {
        UserDto author = userService.getUserProfile(userId);
        return postRepository.findUserPosts(userId, offset, limit)
                .stream()
                .map((post -> new PostDto(post, author)))
                .toList();
    }

    public Post getById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> {
            throw new AppException.PostNotFoundException(id);
        });
    }

    @Transactional
    public PostDto addPost(Post post, User user) {
        Post saved = postRepository.save(post);
        user.incrementPostCount();
        UserDto author = userService.saveUserStats(user.getUserStats());
        return new PostDto(saved, author);
    }

    @Transactional
    public void deletePostById(Long id, User user) {
        getUserPost(id, user);
        postRepository.deleteById(id);
        user.decrementPostCount();
        userService.saveUserStats(user.getUserStats());
    }

    public PostDto updatePost(PostDto.PostUpdate postUpdate, User user) {
        Post post = getUserPost(postUpdate.id, user);
        post.setBody(postUpdate.body);
        post.updateTime();
        Post updatedPost = postRepository.save(post);
        UserDto author = userService.getUserInfo(user);
        return new PostDto(updatedPost, author);
    }

    public PostDto.PostListDto createPostListDto(Post post) {
        UserDto author = userService.getUserInfo(post.getUser());
        return new PostDto.PostListDto(post, author);
    }

    public void favoritePost(User user, Long postId) {
        Post post = getById(postId);
        if (!favUserPostRepository.existsByUserAndPost(user, post)) {
            favUserPostRepository.save(new FavUserPost(user, post));
        }
    }

    public void unfavoritePost(User user, Long postId) {
        Post post = getById(postId);
        List<FavUserPost> favList = favUserPostRepository.findByUserAndPost(user, post);
        if (favList.size() > 0) {
            favUserPostRepository.deleteById(favList.get(0).getId());
        }
    }

    private Post getUserPost(Long id, User user) {
        Post post = getById(id);
        if (!post.getUser().getId().equals(user.getId())) {
            throw new AppException.UnauthorizedException();
        }
        return post;
    }
}
