package com.demo.app.comment;

public class CommentDto {
    public Long id;
    public String body;
    public Long itemId;
    public Long userId;
    public Long createdAt;
    public Long updatedAt;
    public String userName;

    public CommentDto() {
    }

    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.body = comment.getBody();
        this.itemId = comment.getItem().getId();
        this.userId = comment.getUser().getId();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.userName = comment.getUser().getName();
    }

    @Override
    public String toString() {
        return String.format("CommentDto[id=%d, body='%s' item_id=%d user_id=%d created_at='%d', " +
                        "updated_at='%d']", id,
                body, itemId, userId, createdAt, updatedAt);
    }

    public static class CommentCreate {
        public String body;
        public Long postId;

        public CommentCreate(String body, Long postId) {
            this.body = body;
            this.postId = postId;
        }

        @Override
        public String toString() {
            return "CommentCreate[body=" + body + ", postId=" + postId + "]";
        }
    }

    public static class CommentUpdate {
        public Long id;
        public String body;

        public CommentUpdate(String body, Long id) {
            this.body = body;
            this.id = id;
        }

        @Override
        public String toString() {
            return "CommentUpdate[body=" + body + ", id=" + id + "]";
        }
    }
}
