package com.demo.app.comment;

import com.demo.app.post.Post;
import com.demo.app.post.PostService;
import com.demo.app.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostService postService;

    public Comment findById(Long id) {
        Optional<Comment> optional = commentRepository.findById(id);
        Comment comment = null;
        if (optional.isPresent()) {
            comment = optional.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found for id :: " + id);
        }
        return comment;
    }

    public List<Comment> findByPostId(Long itemId) {
        return commentRepository.findByPostId(itemId);
    }

    public List<Comment> findByUserId(Long userId) {
        return commentRepository.findByUserId(userId);
    }

    public List<Comment> findByUserName(String name) {
        return commentRepository.findByUserName(name);
    }

    public List<Comment> findByPostAndUser(Long itemId, Long userId) {
        return commentRepository.findByPostAndUser(itemId, userId);
    }

    public Comment save(String body, Long postId, User user) {
        Post post = postService.getById(postId);
        return commentRepository.save(new Comment(body, post, user));
    }

    public void delete(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow();
        commentRepository.delete(comment);
    }

    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

    public void update(Comment comment, String body) {
        comment.setBody(body);
        commentRepository.save(comment);
    }
}
