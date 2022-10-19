package com.demo.app.comment;

public class CommentDto {
    public Long id;
    public String body;
    public Long postId;
    public Long userId;
    public Long createdAt;
    public Long updatedAt;
    public String userName;

    protected CommentDto() {
    }

    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.body = comment.getBody();
        this.postId = comment.getItem().getId();
        this.userId = comment.getUser().getId();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.userName = comment.getUser().getName();
    }

    @Override
    public String toString() {
        return String.format("CommentDto[id=%d, body='%s' post_id=%d user_id=%d created_at='%d', " +
                        "updated_at='%d']", id,
                body, postId, userId, createdAt, updatedAt);
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

        public CommentUpdate(Long id, String body) {
            this.id = id;
            this.body = body;
        }

        @Override
        public String toString() {
            return "CommentUpdate[body=" + body + ", id=" + id + "]";
        }
    }
}
