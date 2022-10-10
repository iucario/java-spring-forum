package com.demo.app.item;

import com.demo.app.user.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    @Column(name = "text")
    private String body;
    @Column(name = "created_time", columnDefinition = "BIGINT")
    private Long createdAt;
    @Column(name = "updated_time", columnDefinition = "BIGINT")
    private Long updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    protected Item() {
    }

    public Item(String title, String body, User user) {
        this.title = title;
        this.body = body;
        this.user = user;
        final long timestamp = new Date().getTime();
        this.createdAt = timestamp;
        this.updatedAt = timestamp;
    }

    @Override
    public String toString() {
        return String.format("Item[item_id=%d, text='%s', created_time='%d', updated_time='%d']", id,
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
