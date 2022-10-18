package com.demo.app.post;

import org.springframework.lang.NonNull;

public class PostDto {
    public Long id;
    public String title;
    public String body;
    public Long createdAt;
    public Long updatedAt;
    public Long userId;
    public String userName;

    public PostDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.body = post.getText();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.userId = post.getUser().getId();
        this.userName = post.getUser().getName();
    }

    @Override
    public String toString() {
        return String.format("PostDto[id=%d, title='%s' body=%s created_at='%d', updated_at='%d']", id,
                title, body, createdAt, updatedAt);
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
            return "PostCreate[title=" + title + ", body=" + body + "]";
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
            return "PostUpdate[body=" + body + ", id=" + id + "]";
        }
    }
}
