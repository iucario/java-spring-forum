package com.demo.app.comment;

import com.demo.app.post.Post;
import com.demo.app.user.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String body;
    @Column(name = "created_at", columnDefinition = "BIGINT")
    private Long createdAt;
    @Column(name = "updated_at", columnDefinition = "BIGINT")
    private Long updatedAt;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Post post;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    protected Comment() {
    }

    public Comment(String body, Post post, User user) {
        this.body = body;
        this.post = post;
        this.user = user;
        final long timestamp = new Date().getTime();
        this.createdAt = timestamp;
        this.updatedAt = timestamp;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Post getItem() {
        return post;
    }

    public User getUser() {
        return user;
    }

    public Post getPost() {
        return post;
    }

    public Long getId() {
        return id;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void updateTime() {
        this.updatedAt = new Date().getTime();
    }

    public String toString() {
        return String.format("Comment[id=%d, body='%s', created_at='%d', updated_at='%d', user='%s']", id,
                body, createdAt, updatedAt, user.getName());
    }
}
