package com.demo.app.post;

import com.demo.app.common.RedisUtil;
import com.demo.app.exception.AppException;
import com.demo.app.favorite.FavUserPost;
import com.demo.app.favorite.FavUserPostRepository;
import com.demo.app.user.User;
import com.demo.app.user.UserDto;
import com.demo.app.user.UserService;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class PostService {
    final Logger logger = org.slf4j.LoggerFactory.getLogger(PostService.class);
    private final PostRepository postRepository;
    private final UserService userService;
    private final FavUserPostRepository favUserPostRepository;
    private final RedisUtil redisUtil;

    public PostService(PostRepository postRepository, UserService userService,
                       FavUserPostRepository favUserPostRepository, RedisUtil redisUtil) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.favUserPostRepository = favUserPostRepository;
        this.redisUtil = redisUtil;
    }

    /**
     * Explicitly set the commentCount because it's initialized to 0 in the PostDto constructor.
     * Was created to make tests work. Seems that it's not needed. TODO: remove it.
     */
    private PostDto createPostDto(Post post) {
        PostDto postDto = new PostDto(post);
        postDto.commentCount = post.getCommentCount();
        return postDto;
    }

    public List<PostDto> getPostList(final int offset, final int size) {
        List<PostDto> posts = redisUtil.getPostList();
        if (posts.size() < offset + size) {
            // Part or all of the posts are not in the cache, then get all from the database
            return postRepository.findPosts(offset, size)
                    .stream()
                    .sorted(Comparator.comparing(Post::getActiveAt).reversed())
                    .map(this::createPostDto)
                    .toList();
        } else {
            return posts.subList(offset, offset + size);
        }
    }

    public List<PostDto> getUserPosts(Long userId, int offset, int limit) {
        UserDto author = userService.getUserProfile(userId);
        return postRepository.findUserPosts(userId, offset, limit)
                .stream()
                .map(this::createPostDto)
                .toList();
    }

    public Post getById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> {
            throw new AppException.PostNotFoundException(id);
        });
    }

    public PostDto addPost(Post post, User user) {
        Post saved = postRepository.save(post);
        user.incrementPostCount();
        UserDto author = userService.saveUserStats(user.getUserStats());
        PostDto postDto = new PostDto(saved, author);
        redisUtil.addPost(postDto);
        return postDto;
    }

    public void deletePostById(Long id, User user) {
        getUserPost(id, user);
        postRepository.deleteById(id);
        user.decrementPostCount();
        userService.saveUserStats(user.getUserStats());
        redisUtil.deletePost(id);
    }

    public PostDto updatePost(PostDto.PostUpdate update, User user) {
        Post post = getUserPost(update.id, user);
        post.setBody(update.body);
        post.updateTime();
        Post updatedPost = postRepository.save(post);
        UserDto author = userService.getUserInfo(user);
        redisUtil.updatePost(updatedPost);
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

    public void updateActiveAt(Post post, Long activeAt) {
        post.setActiveAt(activeAt);
        postRepository.save(post);
    }

}
