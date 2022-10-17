package com.demo.app.post;

import org.springframework.lang.NonNull;

import java.util.List;

public class PostDto {
    public Long id;
    public String title;
    public String body;
    public Long createdAt;
    public Long updatedAt;
    public List<String> images;
    public Long userId;
    public String userName;

    public PostDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.body = post.getText();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.images = List.of();
        this.userId = post.getUser().getId();
        this.userName = post.getUser().getName();
    }

    @Override
    public String toString() {
        return String.format("PostDto[id=%d, title='%s' body=%s created_at='%d', updated_at='%d']", id,
                title, body, createdAt, updatedAt);
    }

    public static class ItemCreate {
        public String title;
        public String body;
        public String[] images;

        public ItemCreate(String body, String[] images) {
            this.body = body;
            this.images = images;
        }

        @Override
        public String toString() {
            return "PostCreate[title=" + title + ", body=" + body + "]";
        }
    }

    public static class ItemUpdate {
        @NonNull
        public Long id;
        @NonNull
        public String body;

        public ItemUpdate(@NonNull String body, @NonNull Long id) {
            this.body = body;
            this.id = id;
        }

        @Override
        public String toString() {
            return "PostUpdate[body=" + body + ", id=" + id + "]";
        }
    }

    public static class ItemDelete {
        @NonNull
        public Long id;
        // fixme: com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot construct instance of
        //  (although at least one Creator exists): cannot deserialize from Object value

        public ItemDelete(@NonNull Long id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "PostDelete[id=" + id + "]";
        }
    }
}
