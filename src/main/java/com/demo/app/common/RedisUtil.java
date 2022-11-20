package com.demo.app.common;

import com.demo.app.comment.CommentDto;
import com.demo.app.comment.CommentRepository;
import com.demo.app.post.Post;
import com.demo.app.post.PostDto;
import com.demo.app.post.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class RedisUtil {
    final Logger logger = LoggerFactory.getLogger(RedisUtil.class);
    private final RedisTemplate<String, Object> template;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final String POST_INDEX_KEY = "postList"; // postId sorted set by activeAt
    private final String POST_KEY = "post:%d"; // postDto
    private final String COMMENT_KEY = "comment:%d"; // commentDto
    private final String COMMENT_SET_KEY = "post:%d:comments"; // commentId sorted set by createdAt

    public RedisUtil(PostRepository postRepository, CommentRepository commentRepository, RedisTemplate<String,
            Object> redisTemplate) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.template = redisTemplate;

        initCache();
        logger.info("RedisUtil initialized. Cached posts: %d".formatted(getPostList().size()));
    }

    /**
     * Add the last 100 active posts to the cache. Mostly used in testing.
     */
    private void initCache() {
        List<Post> posts = postRepository.findPosts(0, 100);
        List<PostDto> postList = posts
                .stream()
                .map(PostDto::new)
                .toList();
        List<CommentDto> comments = posts
                .stream()
                .flatMap(post -> commentRepository.findAllByPostId(post.getId()).stream()
                        .map(CommentDto::new))
                .toList();
        template.executePipelined((RedisCallback<Object>) connection -> {
            for (PostDto postDto : postList) {
                addPost(postDto);
            }
            for (CommentDto commentDto : comments) {
                addComment(commentDto);
            }
            return null;
        });
    }

    // TODO: delete post detail and get comment list from comment list key
    public void addPost(PostDto post) {
        template.opsForZSet().add(POST_INDEX_KEY, post.id, post.activeAt);
        template.opsForValue().set(POST_KEY.formatted(post.id), post);
    }

    /**
     * Get the first 100 posts from the cache.
     * TODO: add parameters offset and size
     */
    public List<PostDto> getPostList() {
        final int size = 100;
        final int offset = 0;
        List<Object> postList = template.executePipelined((RedisCallback<Object>) connection -> {
            Set<Object> postListIndex = template.opsForZSet().range(POST_INDEX_KEY, offset, size - 1);
            for (Object postId : postListIndex) {
                template.opsForValue().get(POST_KEY.formatted(Long.valueOf(postId.toString())));
            }
            return null;
        });
        return postList.stream().map(PostDto.class::cast).toList();
    }

    /**
     * Delete the post and related comments and sorted index from the cache.
     */
    public void deletePost(Long id) {
        template.opsForZSet().remove(POST_INDEX_KEY, id);
        template.delete(POST_KEY.formatted(id));
        Set<Object> commentIds = template.opsForZSet().range(COMMENT_SET_KEY.formatted(id), 0, -1);
        for (Object commentId : commentIds) {
            template.delete(COMMENT_KEY.formatted(Long.valueOf(commentId.toString())));
        }
    }

    public void updatePost(Post post) {
        template.opsForValue().set(POST_KEY.formatted(post.getId()), new PostDto(post));
    }

    public void addComment(CommentDto commentDto) {
        template.opsForValue().set(COMMENT_KEY.formatted(commentDto.id), commentDto);
        template.opsForZSet().add(COMMENT_SET_KEY.formatted(commentDto.postId), commentDto.id, commentDto.createdAt);
        updateActiveAt(commentDto.postId, commentDto.createdAt);
        updateCommentCount(commentDto.postId);
    }

    private void updateCommentCount(Long postId) {
        PostDto postDto = (PostDto) template.opsForValue().get(POST_KEY.formatted(postId));
        postDto.commentCount = getCommentCount(postId);
        template.opsForValue().set(POST_KEY.formatted(postId), postDto);
    }

    /**
     * Get comment count of a post from the cache. May be inaccurate if the cache is not updated.
     */
    private int getCommentCount(Long postId) {
        String key = COMMENT_SET_KEY.formatted(postId);
        Long count = template.opsForZSet().zCard(key);
        return count != null ? Math.toIntExact(count) : 0;
    }

    private void updateActiveAt(Long postId, Long activeAt) {
        template.opsForZSet().add(POST_INDEX_KEY, postId, activeAt);
    }

    public List<CommentDto> getCommentsByPostId(Long postId, int offset, int limit) {
        List<Object> comments = template.executePipelined((RedisCallback<Object>) connection -> {
            Set<Object> commentIds = template.opsForZSet().range(COMMENT_SET_KEY.formatted(postId), offset,
                    offset + limit);
            assert commentIds != null;
            for (Object commentId : commentIds) {
                connection.get(COMMENT_KEY.formatted(Long.valueOf(commentId.toString())).getBytes());
            }
            return null;
        });
        return comments.stream().map(CommentDto.class::cast).toList();
    }

    public void deleteComment(Long id) {
        CommentDto c = (CommentDto) template.opsForValue().getAndDelete(COMMENT_KEY.formatted(id));
        if (c != null) {
            template.opsForZSet().remove(COMMENT_SET_KEY.formatted(c.postId), id);
        }
    }

    public void updateComment(CommentDto commentDto) {
        template.opsForValue().set(COMMENT_KEY.formatted(commentDto.id), commentDto);
        template.opsForZSet().add(COMMENT_SET_KEY.formatted(commentDto.postId), commentDto.id, commentDto.updatedAt);
    }

    public Boolean hasPost(Long postId) {
        return Boolean.TRUE.equals(template.hasKey(POST_KEY.formatted(postId)));
    }
}
