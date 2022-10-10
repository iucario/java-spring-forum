package com.demo.app.comment;

import com.demo.app.item.Item;
import com.demo.app.user.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String body;
    private Long createdAt;
    private Long updatedAt;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    protected Comment() {
    }

    public Comment(String body, Item item, User user) {
        this.body = body;
        this.item = item;
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

    public Item getItem() {
        return item;
    }

    public User getUser() {
        return user;
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
}
