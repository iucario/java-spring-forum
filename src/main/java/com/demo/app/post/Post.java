package com.demo.app.post;

import com.demo.app.user.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String title;
    private String body;
    @Column(name = "created_at", columnDefinition = "BIGINT")
    private Long createdAt;
    @Column(name = "updated_at", columnDefinition = "BIGINT")
    private Long updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    protected Post() {
    }

    public Post(String title, String body, User user) {
        this.title = title;
        this.body = body;
        this.user = user;
        final long timestamp = new Date().getTime();
        this.createdAt = timestamp;
        this.updatedAt = timestamp;
    }

    @Override
    public String toString() {
        return String.format("Post[item_id=%d, body='%s', created_at='%d', updated_at='%d']", id,
                body, createdAt, updatedAt);
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return body;
    }

    public void setText(String text) {
        this.body = text;
    }

    public User getUser() {
        return user;
    }

    public void updateTime() {
        this.updatedAt = new Date().getTime();
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getTitle() {
        return title;
    }
}
