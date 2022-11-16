package com.demo.app.post;

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
    private final String POST_INDEX_KEY = "postList";
    private final String POST_KEY = "post:%d";
    private final RedisTemplate<String, Object> redisTemplate;

    public PostService(PostRepository postRepository, UserService userService,
                       FavUserPostRepository favUserPostRepository, RedisTemplate<String, Object> redisTemplate) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.favUserPostRepository = favUserPostRepository;
        this.redisTemplate = redisTemplate;
    }

    public List<PostDto> getPostList(final int offset, final int size) {
        List<PostDto> posts = cacheGetPostList();
        if (posts.size() < offset + size) {
            // Part or all of the posts are not in the cache, then get all from the database
            List<PostDto> postList = postRepository.findPosts(offset, size)
                    .stream()
                    .map(PostDto::new)
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
        PostDto postDto = new PostDto(saved, author);
        cacheAddPost(postDto);
        return postDto;
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
        cacheUpdatePost(updatedPost);
        return new PostDto(updatedPost, author);
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
        List<PostDto> postList = postRepository.findPosts(0, 100)
                .stream()
                .map(PostDto::new)
                .toList();
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (PostDto postDto : postList) {
                redisTemplate.opsForZSet().add(POST_INDEX_KEY, postDto.id, postDto.activeAt);
                redisTemplate.opsForValue().set(POST_KEY.formatted(postDto.id), postDto);
            }
            return null;
        });
    }

    // TODO: delete post detail and get comment list from comment list key
    private void cacheAddPost(PostDto post) {
        redisTemplate.opsForZSet().add(POST_INDEX_KEY, post.id, post.activeAt);
        redisTemplate.opsForValue().set(POST_KEY.formatted(post.id), post);
    }

    private List<PostDto> cacheGetPostList() {
        List<Object> postList = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            Set<Object> postListIndex = redisTemplate.opsForZSet().range(POST_INDEX_KEY, 0, 100);
            for (Object postId : postListIndex) {

                redisTemplate.opsForValue().get(POST_KEY.formatted(Long.valueOf(postId.toString())));
            }
            return null;
        });
        return postList.stream().map(PostDto.class::cast).toList();
    }

    private void cacheDeletePost(Long id) {
        redisTemplate.opsForZSet().remove(POST_INDEX_KEY, id);
        redisTemplate.delete(POST_KEY.formatted(id));
    }

    private void cacheUpdatePost(Post post) {
        redisTemplate.opsForValue().set(POST_KEY.formatted(post.getId()), new PostDto(post));
    }

}
