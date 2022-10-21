package com.demo.app.comment;

import com.demo.app.auth.AuthService;
import com.demo.app.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public List<CommentDto> getComments(@RequestParam Long postId, @RequestParam(required = false) Long userId) {
        if (userId != null) {
            return commentService.getByPostAndUser(postId, userId);
        } else {
            return commentService.getByPostId(postId);
        }
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<CommentDto> addComment(@RequestBody CommentDto.CommentCreate commentCreate,
                                                 final HttpServletRequest request) {
        final User user = authService.getUser(request);
        CommentDto commentDto = commentService.addComment(commentCreate, user);
        return new ResponseEntity<>(commentDto, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<String> deleteComment(@PathVariable Long id, final HttpServletRequest request) {
        final User user = authService.getUser(request);
        return commentService.deleteComment(id, user);
    }

    @PutMapping(consumes = "application/json", produces = "application/json")
    public CommentDto updateComment(@RequestBody CommentDto.CommentUpdate commentUpdate,
                                    final HttpServletRequest request) {
        final User user = authService.getUser(request);
        return commentService.updateComment(commentUpdate, user);
    }

}
