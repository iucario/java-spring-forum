package com.demo.app.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public List<Post> getAll(Long userId, int offset) {
        return (List<Post>) postRepository.getAll(userId, offset, 10);
    }

    public Post getById(Long id) {
        Optional<Post> optional = postRepository.findById(id);
        Post post = null;
        if (optional.isPresent()) {
            post = optional.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found for id :: " + id);
        }
        return post;
    }

    public Post addItem(Post post) {
        return this.postRepository.save(post);
    }

    public void deleteItemById(Long id) {
        this.postRepository.deleteById(id);
    }

    @Transactional
    public void updateItem(Post post) {
        postRepository.findById(post.getId()).ifPresent(existingItem -> {
            existingItem.setText(post.getText());
            existingItem.updateTime();
        });
    }
}
