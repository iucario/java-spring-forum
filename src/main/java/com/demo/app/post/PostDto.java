package com.demo.app.post;

import com.demo.app.user.UserDto;
import org.springframework.lang.NonNull;

public class PostDto {
    public Long id;
    public String title;
    public String body;
    public Long createdAt;
    public Long updatedAt;
    public UserDto author;

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

    public static class PostCreate {
        public String title;
        public String body;

        public PostCreate(String title, String body) {
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
        public String body;

        public PostUpdate(@NonNull String body, @NonNull Long id) {
            this.body = body;
            this.id = id;
        }

        @Override
        public String toString() {
            return String.format("PostUpdate[body=%s, id=%d]", body, id);
        }
    }
}
