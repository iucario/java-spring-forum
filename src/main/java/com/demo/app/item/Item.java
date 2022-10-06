package com.demo.app.item;

import com.demo.app.user.User;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String text;
    @Column(name = "created_time", columnDefinition = "BIGINT")
    private Long createdAt;
    @Column(name = "updated_time", columnDefinition = "BIGINT")
    private Long updatedAt;

    @OneToMany(mappedBy = "item")
    private Set<Image> images;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    protected Item() {
    }

    public Item(String text) {
        this.text = text;
        final var timestamp = new Date().getTime();
        this.createdAt = timestamp;
        this.updatedAt = timestamp;
    }

    public Item(String text, String[] images, User user) {
        this.text = text;
        final var timestamp = new Date().getTime();
        this.createdAt = timestamp;
        this.updatedAt = timestamp;
        this.user = user;
    }

    @Override
    public String toString() {
        return String.format("Item[item_id=%d, text='%s', images='%s', created_time='%d', updated_time='%d']", id,
                text, images, createdAt, updatedAt);
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Set<Image> getImages() {
        return images;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
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

    public static class ItemCreate {
        public String text;
        public String[] images;
        public Long user_id;

        public ItemCreate(String text, String[] images, Long user_id) {
            this.text = text;
            this.images = images;
            this.user_id = user_id;
        }

    }

    public static class ItemUpdate {
        public String text;
        public Long item_id;
        public Long user_id;

        public ItemUpdate(String text, Long item_id, Long user_id) {
            this.text = text;
            this.user_id = user_id;
            this.item_id = item_id;
        }

    }
}
