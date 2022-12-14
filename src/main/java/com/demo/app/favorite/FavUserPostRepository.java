package com.demo.app.favorite;

import com.demo.app.post.Post;
import com.demo.app.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavUserPostRepository extends JpaRepository<FavUserPost, Long> {

    @Query("SELECT f FROM FavUserPost f WHERE f.user.id = :userId")
    List<FavUserPost> findByUser(@Param(value = "userId") Long userId);

    @Query("SELECT f FROM FavUserPost f WHERE f.post.id = :postId")
    List<FavUserPost> findByPost(@Param(value = "postId") Long PostId);

    Boolean existsByUserAndPost(User user, Post post);

    List<FavUserPost> findByUserAndPost(User user, Post post);
}
