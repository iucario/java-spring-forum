package com.demo.app.comment;

import com.demo.app.auth.AuthService;
import com.demo.app.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;
    private final AuthService authService;

    CommentController(CommentService commentService, AuthService authService) {
        this.commentService = commentService;
        this.authService = authService;
    }

    @GetMapping(produces = "application/json")
    public List<CommentDto> getComments(@RequestParam Long postId, @RequestParam(required = false) Long userId,
                                        @RequestParam(required = false, defaultValue = "0") Integer offset,
                                        @RequestParam(required = false, defaultValue = "20") Integer size) {
        if (userId != null) {
            return commentService.getByPostAndUser(postId, userId);
        } else {
            return commentService.getByPostId(postId, offset, size);
        }
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public CommentDto addComment(@RequestBody CommentDto.CommentCreate commentCreate) {
        final User user = authService.getCurrentUser();
        CommentDto commentDto = commentService.addComment(commentCreate, user);
        return commentDto;
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity deleteComment(@PathVariable Long id) {
        final User user = authService.getCurrentUser();
        commentService.deleteComment(id, user);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(consumes = "application/json", produces = "application/json")
    public CommentDto updateComment(@RequestBody CommentDto.CommentUpdate commentUpdate) {
        final User user = authService.getCurrentUser();
        return commentService.updateComment(commentUpdate, user);
    }

}
