package com.demo.app.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(value = "SELECT * FROM posts p WHERE p.user_id = :userId ORDER BY created_at DESC LIMIT :limit OFFSET " +
            ":offset",
            nativeQuery = true)
    List<Post> findUserPosts(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);

    @Query("SELECT COUNT(*) FROM Post i WHERE i.user.id = :userId")
    int countUserPosts(@Param("userId") Long userId);
}