package com.demo.app.favorite;

import com.demo.app.post.Post;
import com.demo.app.user.User;

import javax.persistence.*;

@Entity
@Table(name = "fav_user_post", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "post_id"})
})
public class FavUserPost {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fav_user_post_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    protected FavUserPost() {
    }

    public FavUserPost(User user, Post post) {
        this.user = user;
        this.post = post;
    }

    @Override
    public String toString() {
        return String.format("FavUserPost[user=%s, post=%s]", user.getName(), post.getTitle());
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
