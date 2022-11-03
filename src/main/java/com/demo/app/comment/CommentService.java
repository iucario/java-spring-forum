package com.demo.app.comment;

import com.demo.app.exception.AppException;
import com.demo.app.post.Post;
import com.demo.app.post.PostService;
import com.demo.app.user.User;
import com.demo.app.user.UserDto;
import com.demo.app.user.UserService;
import com.demo.app.user.userStats.UserStats;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, PostService postService, UserService userService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
        this.userService = userService;
    }

    public Comment getById(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found for id :: " + id);
        });
    }

    public UserDto getPostAuthor(Long postId) {
        User user = userService.getById(postService.getById(postId).getUser().getId());
        return new UserDto(user, new UserStats(user));
    }

    public UserDto getCommentAuthor(Long commentId) {
        User user = getById(commentId).getUser();
        return new UserDto(user, new UserStats(user));
    }

    private CommentDto createCommentDto(Comment comment) {
        UserDto author = getCommentAuthor(comment.getId());
        return new CommentDto(comment, author);
    }

    public List<CommentDto> getByPostId(Long postId, int offset, int limit) {
        return commentRepository.findByPostId(postId, offset, limit)
                .stream()
                .map((this::createCommentDto))
                .toList();
    }

    public List<Comment> getByUserId(Long userId) {
        return commentRepository.findByUserId(userId);
    }

    public List<Comment> getByUserName(String name) {
        return commentRepository.findByUserName(name);
    }

    public List<CommentDto> getByPostAndUser(Long postId, Long userId) {
        UserDto author = userService.getUserProfile(userId);
        return commentRepository.findByPostAndUser(postId, userId)
                .stream()
                .map((comment -> new CommentDto(comment, author)))
                .toList();
    }

    @Transactional
    public CommentDto addComment(CommentDto.CommentCreate commentCreate, User user) {
        Post post = postService.getById(commentCreate.postId);
        Comment comment = commentRepository.save(new Comment(commentCreate.body, post, user));
        user.incrementCommentCount();
        UserDto author = userService.saveUserStats(user.getUserStats());
        return new CommentDto(comment, author);
    }

    @Transactional
    public void deleteComment(Long id, User user) {
        getUserComment(id, user);
        commentRepository.deleteById(id);
        user.decrementCommentCount();
        userService.saveUserStats(user.getUserStats());
    }

    public CommentDto updateComment(CommentDto.CommentUpdate commentUpdate, User user) {
        Comment comment = getUserComment(commentUpdate.id, user);
        comment.setBody(commentUpdate.body);
        comment.updateTime();
        UserDto author = userService.getUserInfo(user);
        return new CommentDto(commentRepository.save(comment), author);
    }

    private Comment getUserComment(Long id, User user) {
        Comment comment = getById(id);
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new AppException.UnauthorizedException();
        }
        return comment;
    }
}
