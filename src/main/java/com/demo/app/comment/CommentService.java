package com.demo.app.comment;

import com.demo.app.exception.AppException;
import com.demo.app.post.Post;
import com.demo.app.post.PostService;
import com.demo.app.user.User;
import com.demo.app.user.UserDto;
import com.demo.app.user.UserService;
import com.demo.app.user.userStats.UserStats;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserService userService;
    private final String COMMENT_KEY = "comment:%d";
    private final String COMMENT_SET_KEY = "post:%d:comments";
    private final RedisTemplate<String, Object> redisTemplate;

    public CommentService(CommentRepository commentRepository, PostService postService, UserService userService,
                          RedisTemplate<String, Object> redisTemplate) {
        this.commentRepository = commentRepository;
        this.postService = postService;
        this.userService = userService;
        this.redisTemplate = redisTemplate;
    }

    public Comment getById(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found for id :: " + id);
        });
    }

    public UserDto getCommentAuthor(Long commentId) {
        User user = getById(commentId).getUser();
        return new UserDto(user, new UserStats(user));
    }

    public List<CommentDto> getByPostId(Long postId, int offset, int limit) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey("post:%d".formatted(postId)))) {
            return cacheGetCommentByPostId(postId, offset, limit);
        }
        return commentRepository.findByPostId(postId, offset, limit)
                .stream()
                .map(CommentDto::new)
                .toList();
    }

    public List<CommentDto> getByPostAndUser(Long postId, Long userId) {
        UserDto author = userService.getUserProfile(userId);
        return commentRepository.findByPostAndUser(postId, userId)
                .stream()
                .map((comment -> new CommentDto(comment, author)))
                .toList();
    }

    // TODO: update post list in cache. Add to the beginning of the list
    @Transactional
    public CommentDto addComment(CommentDto.CommentCreate commentCreate, User user) {
        Post post = postService.getById(commentCreate.postId);
        Comment comment = commentRepository.save(new Comment(commentCreate.body, post, user));
        user.incrementCommentCount();
        UserDto author = userService.saveUserStats(user.getUserStats());
        CommentDto commentDto = new CommentDto(comment, author);
        cacheAddComment(commentDto);
        return commentDto;
    }

    @Transactional
    public void deleteComment(Long id, User user) {
        getUserComment(id, user);
        commentRepository.deleteById(id);
        user.decrementCommentCount();
        userService.saveUserStats(user.getUserStats());
        cacheDeleteComment(id);
    }

    public CommentDto updateComment(CommentDto.CommentUpdate commentUpdate, User user) {
        Comment comment = getUserComment(commentUpdate.id, user);
        comment.setBody(commentUpdate.body);
        comment.updateTime();
        CommentDto commentDto = new CommentDto(commentRepository.save(comment));
        cacheUpdateComment(commentDto);
        return commentDto;
    }

    private Comment getUserComment(Long id, User user) {
        Comment comment = getById(id);
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new AppException.UnauthorizedException();
        }
        return comment;
    }

    private void cacheAddComment(CommentDto comment) {
        redisTemplate.opsForValue().set(COMMENT_KEY.formatted(comment.id), comment);
        redisTemplate.opsForZSet().add(COMMENT_SET_KEY.formatted(comment.postId), comment.id, comment.updatedAt);

    }

    private List<CommentDto> cacheGetCommentByPostId(Long postId, int offset, int limit) {
        List<Object> comments = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            Set<Object> commentIds = redisTemplate.opsForZSet().range(COMMENT_SET_KEY.formatted(postId), offset,
                    offset + limit);
            assert commentIds != null;
            for (Object commentId : commentIds) {
                connection.get(COMMENT_KEY.formatted(Long.valueOf(commentId.toString())).getBytes());
            }
            return null;
        });
        return comments.stream().map(CommentDto.class::cast).toList();
    }

    private void cacheDeleteComment(Long id) {
        CommentDto c = (CommentDto) redisTemplate.opsForValue().getAndDelete(COMMENT_KEY.formatted(id));
        if (c != null) {
            redisTemplate.opsForZSet().remove(COMMENT_SET_KEY.formatted(c.postId), id);
        }
    }

    private void cacheUpdateComment(CommentDto comment) {
        redisTemplate.opsForValue().set(COMMENT_KEY.formatted(comment.id), comment);
        redisTemplate.opsForZSet().add(COMMENT_SET_KEY.formatted(comment.postId), comment.id, comment.updatedAt);
    }
}
