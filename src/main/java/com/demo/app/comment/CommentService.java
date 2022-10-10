package com.demo.app.comment;

import com.demo.app.item.Item;
import com.demo.app.item.ItemService;
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
    private ItemService itemService;

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

    public List<Comment> findByItemId(Long itemId) {
        return commentRepository.findByItemId(itemId);
    }

    public List<Comment> findByUserId(Long userId) {
        return commentRepository.findByUserId(userId);
    }

    public List<Comment> findByUserName(String name) {
        return commentRepository.findByUserName(name);
    }

    public List<Comment> findByItemAndUser(Long itemId, Long userId) {
        return commentRepository.findByItemAndUser(itemId, userId);
    }

    public Comment save(String body, Long itemId, User user) {
        Item item = itemService.getById(itemId);
        return commentRepository.save(new Comment(body, item, user));
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
