package com.demo.app.comment;

import com.demo.app.post.Post;
import com.demo.app.post.PostService;
import com.demo.app.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;

    public CommentService(CommentRepository commentRepository, PostService postService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
    }

    public Comment getById(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found for id :: " + id);
        });
    }

    public List<Comment> getByPostId(Long itemId) {
        return commentRepository.findByPostId(itemId);
    }

    public List<Comment> getByUserId(Long userId) {
        return commentRepository.findByUserId(userId);
    }

    public List<Comment> getByUserName(String name) {
        return commentRepository.findByUserName(name);
    }

    public List<Comment> getByPostAndUser(Long itemId, Long userId) {
        return commentRepository.findByPostAndUser(itemId, userId);
    }

    public Comment addComment(CommentDto.CommentCreate commentCreate, User user) {
        Post post = postService.getById(commentCreate.postId);
        return commentRepository.save(new Comment(commentCreate.body, post, user));
    }

    public ResponseEntity<String> deleteComment(Long id, User user) {
        Comment comment = getById(id);
        if (!comment.getUser().getId().equals(user.getId())) {
            System.out.println("Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        commentRepository.deleteById(id);
        return ResponseEntity.ok("Deleted %d".formatted(id));

    }

    public ResponseEntity<CommentDto> updateComment(CommentDto.CommentUpdate commentUpdate, User user) {
        Comment comment = getById(commentUpdate.id);
        if (!comment.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        comment.setBody(commentUpdate.body);
        commentRepository.save(comment);
        return ResponseEntity.ok(new CommentDto(comment));
    }
}
