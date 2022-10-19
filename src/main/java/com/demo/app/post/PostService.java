package com.demo.app.post;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<PostDto> getAllPosts(Long userId, int offset, int limit) {
        return postRepository.getAll(userId, offset, limit).stream().map(PostDto::new).toList();
    }

    public int countUserPosts(Long userId) {
        return postRepository.countAll(userId);
    }

    public Post getById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found for id :: " + id);
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found for id :: " + postUpdate.id);
        });
        existingPost.setBody(postUpdate.body);
        existingPost.updateTime();
        Post updatedPost = postRepository.save(existingPost);
        return new PostDto(updatedPost);
    }
}
