package com.demo.app.user;

import com.demo.app.favorite.FavUserPost;
import com.demo.app.post.Post;
import com.demo.app.user.userStats.UserStats;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    private Long id;
    @Column(unique = true)
    private String name;
    @Column(name = "hashed_password")
    private String hashedPassword;
    @Column(name = "created_at", columnDefinition = "BIGINT")
    private Long createdAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Post> posts;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Post> comments;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserStats userStats;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<FavUserPost> favUserPosts;

    public User() {
    }

    public User(String name, String password) {
        this.name = name;
        this.hashedPassword = password;
        this.createdAt = new Date().getTime();
    }

    @Override
    public String toString() {
        return String.format("User[id=%d, name=%s, created_at=%d]", id, name, createdAt);
    }

    public UserStats incrementPostCount() {
        userStats.setPostCount(userStats.getPostCount() + 1);
        return userStats;
    }

    public UserStats incrementCommentCount() {
        userStats.setCommentCount(userStats.getCommentCount() + 1);
        return userStats;
    }

    public UserStats incrementFileCount() {
        userStats.setFileCount(userStats.getFileCount() + 1);
        return userStats;
    }

    public UserStats decrementPostCount() {
        userStats.setPostCount(userStats.getPostCount() - 1);
        return userStats;
    }

    public UserStats decrementCommentCount() {
        userStats.setCommentCount(userStats.getCommentCount() - 1);
        return userStats;
    }

    public void decrementFileCount() {
        userStats.setFileCount(userStats.getFileCount() - 1);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public UserStats getUserStats() {
        return userStats;
    }

    public void setUserStats(UserStats userStats) {
        this.userStats = userStats;
    }

    public void setUserStats(Long postCount, Long commentCount, Long fileCount) {
        this.userStats.setPostCount(postCount);
        this.userStats.setCommentCount(commentCount);
        this.userStats.setFileCount(fileCount);
    }

}
