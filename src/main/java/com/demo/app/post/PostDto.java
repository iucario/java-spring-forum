package com.demo.app.post;

import com.demo.app.comment.CommentDto;
import com.demo.app.user.UserDto;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Pattern;
import java.util.List;

public class PostDto {
    public Long id;
    public String title;
    public String body;
    public Long createdAt;
    public Long updatedAt;
    public Long activeAt;
    public UserDto author;
    public int commentCount = 0;

    public PostDto() {
    }

    public PostDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.body = post.getBody();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.activeAt = post.getActiveAt();
        this.author = new UserDto(post.getUser());
        this.commentCount = 0;
    }

    public PostDto(Post post, UserDto author) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.body = post.getBody();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.activeAt = post.getActiveAt();
        this.author = author;
        this.commentCount = 0;
    }

    @Override
    public String toString() {
        return String.format("PostDto[id=%d, title=%s, body=%s, created_at=%d, updated_at=%d, active_at=%d, author=%s]"
                , id, title, body, createdAt, updatedAt, activeAt, author.name);
    }

    /**
     * The schema for any data needed in the post detail page. E.g. post content, author, comments, etc.
     */
    public static class PostDetail {
        public Long id;
        public String title;
        public String body;
        public Long createdAt;
        public Long updatedAt;
        public Long activeAt;
        public UserDto author;
        public int commentCount = 0;
        public List<CommentDto> comments;

        public PostDetail() {
        }

        public PostDetail(Post post, List<CommentDto> comments) {
            this.id = post.getId();
            this.title = post.getTitle();
            this.body = post.getBody();
            this.createdAt = post.getCreatedAt();
            this.updatedAt = post.getUpdatedAt();
            this.activeAt = post.getActiveAt();
            this.author = new UserDto(post.getUser());
            this.comments = comments;
            this.commentCount = comments.size();
        }

        @Override
        public String toString() {
            return String.format("PostPage[id=%d, title=%s, created_at=%d, updated_at=%d, author=%s, " +
                            "commentCount=%d]", id,
                    title, createdAt, updatedAt, author.name, commentCount);
        }
    }

    public static class PostCreate {
        @NonNull
        public String title;
        @NonNull
        public String body;

        public PostCreate(@NonNull String title, @NonNull String body) {
            this.title = title;
            this.body = body;
        }

        @Override
        public String toString() {
            return String.format("PostCreate[title=%s, body=%s]", title, body);
        }
    }

    public static class PostUpdate {
        @NonNull
        public Long id;
        @NonNull
        @Pattern(regexp = "favorite|unfavorite|update")
        public String action;
        public String body;

        public PostUpdate(@NonNull String action, @NonNull Long id, @Nullable String body) {
            this.action = action;
            this.id = id;
            this.body = body;
        }

        @Override
        public String toString() {
            return String.format("PostUpdate[body=%s, id=%d]", action, id);
        }
    }

}
