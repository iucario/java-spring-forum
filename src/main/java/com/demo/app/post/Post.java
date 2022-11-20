package com.demo.app.post;

import com.demo.app.comment.Comment;
import com.demo.app.favorite.FavUserPost;
import com.demo.app.user.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_seq")
    private Long id;
    private String title;
    private String body;
    @Column(name = "created_at", columnDefinition = "BIGINT")
    private Long createdAt;
    @Column(name = "updated_at", columnDefinition = "BIGINT")
    private Long updatedAt;
    @Column(name = "active_at", columnDefinition = "BIGINT")
    private Long activeAt; // last time comment was added

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Comment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<FavUserPost> favUserPosts;

    public Post() {
    }

    public Post(String title, String body, User user) {
        this.title = title;
        this.body = body;
        this.user = user;
        final long timestamp = new Date().getTime();
        this.createdAt = timestamp;
        this.updatedAt = timestamp;
        this.activeAt = timestamp;
    }

    public int getCommentCount() {
        return comments.size();
    }

    @Override
    public String toString() {
        return String.format("Post[post_id=%d, body=%s, created_at=%d, updated_at=%d, activeAt=%d, user=%s]", id,
                body, createdAt, updatedAt, activeAt, user.getName());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getActiveAt() {
        return activeAt;
    }

    public void setActiveAt(Long activeAt) {
        this.activeAt = activeAt;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

}
