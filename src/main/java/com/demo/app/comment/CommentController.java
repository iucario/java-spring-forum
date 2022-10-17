package com.demo.app.comment;

import com.demo.app.user.User;
import com.demo.app.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;

    @GetMapping(produces = "application/json")
    public List<CommentDto> getComments(@RequestParam Long postId, @RequestParam(required = false) Long userId,
                                        final HttpServletRequest request) throws Exception {
        List<Comment> commentList;
        if (userId != null) {
            commentList = commentService.findByPostAndUser(postId, userId);
        } else {
            commentList = commentService.findByPostId(postId);
        }
        return commentList.stream().map(CommentDto::new).toList();
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<CommentDto.CommentCreate> addComment(@RequestBody CommentDto.CommentCreate commentCreate,
                                                               final HttpServletRequest request) throws Exception {
        final User user = userService.getUser(request);
        commentService.save(commentCreate.body, commentCreate.postId, user);
        return new ResponseEntity<>(commentCreate, HttpStatus.CREATED); // todo: response
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<String> deleteComment(@PathVariable Long id, final HttpServletRequest request) throws Exception {
        final User user = userService.getUser(request);
        try {
            Comment comment = commentService.findById(id);
            if (!comment.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }
            commentService.deleteById(id);
            return ResponseEntity.ok("Deleted %d".formatted(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
    }

    @PutMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<CommentDto> updateComment(@RequestBody CommentDto.CommentUpdate comment,
                                                    final HttpServletRequest request) throws Exception {
        final User user = userService.getUser(request);
        try {
            Comment c = commentService.findById(comment.id);
            if (!c.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            commentService.update(c, comment.body);
            return ResponseEntity.ok(new CommentDto(c));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    }

}
