package com.demo.app.comment;

import com.demo.app.user.UserDto;

public class CommentDto {
    public Long id;
    public String body;
    public Long postId;
    public Long userId;
    public Long createdAt;
    public Long updatedAt;
    public UserDto author;

    protected CommentDto() {
    }

    public CommentDto(Comment comment, UserDto author) {
        this.id = comment.getId();
        this.body = comment.getBody();
        this.postId = comment.getItem().getId();
        this.userId = comment.getUser().getId();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.author = author;
    }

    @Override
    public String toString() {
        return String.format("CommentDto[id=%d, body=%s, post_id=%d, username=%s, created_at=%d, updated_at=%d]", id,
                body, postId, author.name, createdAt, updatedAt);
    }

    public static class CommentCreate {
        public Long id;
        public String body;
        public Long postId;

        public CommentCreate(String body, Long postId) {
            this.body = body;
            this.postId = postId;
        }

        @Override
        public String toString() {
            return String.format("CommentCreate[id=%d, body=%s, post_id=%d]", id, body, postId);
        }
    }

    public static class CommentUpdate {
        public Long id;
        public String body;

        public CommentUpdate(Long id, String body) {
            this.id = id;
            this.body = body;
        }

        @Override
        public String toString() {
            return String.format("CommentUpdate[body=%s, id=%s]", body, id);
        }
    }
}
