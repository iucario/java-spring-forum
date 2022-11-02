package com.demo.app.post;

import com.demo.app.exception.AppException;
import com.demo.app.user.User;
import com.demo.app.user.UserDto;
import com.demo.app.user.UserService;
import com.demo.app.user.userStats.UserStats;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;

    public PostService(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    public List<PostDto> getUserPosts(Long userId, int offset, int limit) {
        UserDto author = userService.getUserProfile(userId);
        return postRepository.findUserPosts(userId, offset, limit).stream().map((post -> new PostDto(post, author))).toList();
    }

    public Post getById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> {
            throw new AppException.NotFoundException(String.format("Post not found for id %s", id));
        });
    }

    public PostDto addPost(Post post, User user) {
        UserDto author = new UserDto(user, new UserStats(user));
        return new PostDto(postRepository.save(post), author);
    }

    public void deletePostById(Long id) {
        postRepository.deleteById(id);
    }

    public PostDto updatePost(PostDto.PostUpdate postUpdate, User user) {
        Post existingPost = this.postRepository.findById(postUpdate.id).orElseThrow(() -> {
            throw new AppException.NotFoundException(String.format("Post not found for id %s", postUpdate.id));
        });
        existingPost.setBody(postUpdate.body);
        existingPost.updateTime();
        Post updatedPost = postRepository.save(existingPost);
        UserDto author = new UserDto(user, new UserStats(user));
        return new PostDto(updatedPost, author);
    }
}
