package com.demo.app.post;

import com.demo.app.exception.AppException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<PostDto> getUserPosts(Long userId, int offset, int limit) {
        return postRepository.findUserPosts(userId, offset, limit).stream().map(PostDto::new).toList();
    }

    public Post getById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> {
            throw new AppException.NotFoundException(String.format("Post not found for id %s", id));
        });
    }

    public PostDto addPost(Post post) {
        return new PostDto(postRepository.save(post));
    }

    public void deletePostById(Long id) {
        postRepository.deleteById(id);
    }

    public PostDto updatePost(PostDto.PostUpdate postUpdate) {
        Post existingPost = this.postRepository.findById(postUpdate.id).orElseThrow(() -> {
            throw new AppException.NotFoundException(String.format("Post not found for id %s", postUpdate.id));
        });
        existingPost.setBody(postUpdate.body);
        existingPost.updateTime();
        Post updatedPost = postRepository.save(existingPost);
        return new PostDto(updatedPost);
    }
}
