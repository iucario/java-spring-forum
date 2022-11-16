package com.demo.app.post;

import com.demo.app.comment.CommentDto;
import com.demo.app.exception.AppException;
import com.demo.app.favorite.FavUserPost;
import com.demo.app.favorite.FavUserPostRepository;
import com.demo.app.user.User;
import com.demo.app.user.UserDto;
import com.demo.app.user.UserService;
import org.slf4j.Logger;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

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

    public List<PostDto.PostListDto> getPostList(final int offset, final int size) {
        List<PostDto.PostListDto> posts = cacheGetPostList();
        if (posts.size() < offset + size) {
            // Part or all of the posts are not in the cache, then get all from the database
            List<PostDto.PostListDto> postList = postRepository.findPosts(offset, size)
                    .stream()
                    .map(this::createPostListDto)
                    .toList();
            return postList;
        } else {
            return posts.subList(offset, offset + size);
        }
    }

    public List<PostDto> getUserPosts(Long userId, int offset, int limit) {
        UserDto author = userService.getUserProfile(userId);
        return postRepository.findUserPosts(userId, offset, limit)
                .stream()
                .map((post -> new PostDto(post, author)))
                .toList();
    }

    public PostDto.PostDetail getPostDetail(Long id) {
        Post post = getById(id);
        List<CommentDto> comments = post.getComments().stream().map(comment -> {
            UserDto author = userService.getUserInfo(comment.getUser());
            return new CommentDto(comment, author);
        }).toList();
        return new PostDto.PostDetail(post, post.getUser(), comments);
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
        // TODO: Add the post content to cache
        cacheAddPost(post);
        return new PostDto(saved, author);
    }

    @Transactional
    public void deletePostById(Long id, User user) {
        getUserPost(id, user);
        postRepository.deleteById(id);
        user.decrementPostCount();
        userService.saveUserStats(user.getUserStats());
        cacheDeletePost(id);
    }

    public PostDto updatePost(PostDto.PostUpdate update, User user) {
        Post post = getUserPost(update.id, user);
        post.setBody(update.body);
        post.updateTime();
        Post updatedPost = postRepository.save(post);
        UserDto author = userService.getUserInfo(user);
        cacheUpdatePost(update, updatedPost);
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

    /**
     * Add the last 100 active posts to the cache. Mostly not used because the cache should be updated
     * when a post's activeAt is updated.
     */
    private void cachePostList() {
        List<PostDto.PostListDto> postList = postRepository.findPosts(0, 100)
                .stream()
                .map(this::createPostListDto)
                .toList();
        logger.info("Initialize key 'postList' to the cache");
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (PostDto.PostListDto postListDto : postList) {
                redisTemplate.opsForZSet().add("postList", postListDto, postListDto.activeAt);
            }
            return null;
        });
    }

    private void cacheAddPost(Post post) {
        PostDto.PostDetail postDetail = new PostDto.PostDetail(post, post.getUser(), List.of());
        redisTemplate.opsForZSet().add("postListIndex", post.getId(), post.getActiveAt());
        redisTemplate.opsForSet().add("postList:%d".formatted(post.getId()), createPostListDto(post));
        redisTemplate.opsForSet().add("post:%d".formatted(post.getId()), postDetail);
    }

    private List<PostDto.PostListDto> cacheGetPostList() {
        List<Object> postList = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            Set<Object> postListIndex = redisTemplate.opsForZSet().range("postListIndex", 0, 100);
            for (Object postId : postListIndex) {
                redisTemplate.opsForSet().members("postList:%d".formatted(postId));
            }
            return null;
        });
        return postList.stream().map(PostDto.PostListDto.class::cast).toList();
    }

    private void cacheDeletePost(Long id) {
        final String postListKey = "postList:%d";
        final String postKey = "post:%d";
        redisTemplate.opsForZSet().remove("postListIndex", id);
        redisTemplate.delete(postListKey.formatted(id));
        redisTemplate.delete(postKey.formatted(id));
    }

    private void cacheUpdatePost(PostDto.PostUpdate update, Post post) {
        cacheDeletePost(update.id);
        cacheAddPost(post);
    }

}
