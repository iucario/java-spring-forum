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
            commentList = commentService.getByPostAndUser(postId, userId);
        } else {
            commentList = commentService.getByPostId(postId);
        }
        return commentList.stream().map(CommentDto::new).toList();
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<CommentDto.CommentCreate> addComment(@RequestBody CommentDto.CommentCreate commentCreate,
                                                               final HttpServletRequest request) {
        final User user = userService.getUser(request);
        commentService.addComment(commentCreate, user);
        return new ResponseEntity<>(commentCreate, HttpStatus.CREATED); // todo: response
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<String> deleteComment(@PathVariable Long id, final HttpServletRequest request) {
        final User user = userService.getUser(request);
        return commentService.deleteComment(id, user);
    }

    @PutMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<CommentDto> updateComment(@RequestBody CommentDto.CommentUpdate comment,
                                                    final HttpServletRequest request) {
        final User user = userService.getUser(request);
        return commentService.updateComment(comment, user);
    }

}
