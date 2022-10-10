package com.demo.app.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.item.id = :itemId")
    List<Comment> findByItemId(@Param("itemId") Long itemId);

    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId")
    List<Comment> findByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Comment c WHERE c.user.name = :name")
    List<Comment> findByUserName(@Param("name") String name);

    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId AND c.item.id = :itemId")
    List<Comment> findByItemAndUser(@Param("itemId") Long itemId, @Param("userId") Long userId);
}
