package com.demo.app.post;

import com.demo.app.exception.AppException;
import com.demo.app.user.User;
import com.demo.app.user.UserDto;
import com.demo.app.user.UserService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;

    public PostService(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    public List<PostDto.PostListDto> getPostList(final int offset, final int limit) {
        return postRepository.findPosts(offset, limit)
                .stream()
                .map(this::createPostListDto)
                .toList();
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

    private Post getUserPost(Long id, User user) {
        Post post = getById(id);
        if (!post.getUser().getId().equals(user.getId())) {
            throw new AppException.UnauthorizedException();
        }
        return post;
    }

    public PostDto.PostListDto createPostListDto(Post post) {
        UserDto author = userService.getUserInfo(post.getUser());

        return new PostDto.PostListDto(post, author);
    }
}
