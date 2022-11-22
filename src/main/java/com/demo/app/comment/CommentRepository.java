package com.demo.app.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "SELECT * FROM comments c WHERE c.post_id = :postId ORDER BY created_at DESC LIMIT :limit OFFSET " +
            ":offset", nativeQuery = true)
    List<Comment> findByPostId(@Param("postId") Long itemId, @Param("offset") int offset, @Param("limit") int limit);

    @Query(value = "SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(@Param("postId") Long postId);

    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId")
    List<Comment> findByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Comment c WHERE c.user.name = :name")
    List<Comment> findByUserName(@Param("name") String name);

    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId AND c.post.id = :postId")
    List<Comment> findByPostAndUser(@Param("postId") Long postId, @Param("userId") Long userId);
}
