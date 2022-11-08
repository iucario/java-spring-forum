package com.demo.app.post;

import com.demo.app.user.UserDto;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Pattern;

public class PostDto {
    public Long id;
    public String title;
    public String body;
    public Long createdAt;
    public Long updatedAt;
    public UserDto author;
    public Long commentCount = 0L;

    public PostDto(Post post, UserDto author) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.body = post.getBody();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.author = author;
    }

    @Override
    public String toString() {
        return String.format("PostDto[id=%d, title=%s body=%s created_at=%d, updated_at=%d, author=%s]", id,
                title, body, createdAt, updatedAt, author.name);
    }

    public static class PostListDto {
        public Long id;
        public String title;
        public Long createdAt;
        public Long updatedAt;
        public UserDto author;
        public Long commentCount = 0L;

        public PostListDto(Post post, UserDto author) {
            this.id = post.getId();
            this.title = post.getTitle();
            this.createdAt = post.getCreatedAt();
            this.updatedAt = post.getUpdatedAt();
            this.author = author;
        }

        @Override
        public String toString() {
            return String.format("PostListDto[id=%d, title=%s created_at=%d, updated_at=%d, author=%s]", id,
                    title, createdAt, updatedAt, author.name);
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
