package com.demo.app.user.userStats;

import com.demo.app.user.User;

import javax.persistence.*;

@Entity
@Table(name = "user_stats")
public class UserStats {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_stats_seq")
    private Long id;
    @Column(name = "post_count")
    private Long postCount;
    @Column(name = "comment_count")
    private Long commentCount;
    @Column(name = "file_count")
    private Long fileCount;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    protected UserStats() {
    }

    public UserStats(User user) {
        this.postCount = 0L;
        this.commentCount = 0L;
        this.fileCount = 0L;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setPostCount(Long postCount) {
        this.postCount = postCount;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    public Long getPostCount() {
        return postCount;
    }

    public Long getFileCount() {
        return fileCount;
    }

    public void setFileCount(Long fileCount) {
        this.fileCount = fileCount;
    }

    @Override
    public String toString() {
        return String.format("UserStats[id=%d, post_count=%d, comment_count=%d, file_count=%d, user=%s]",
                id, postCount, commentCount, fileCount, user.getName());
    }
}
