package com.demo.app.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "SELECT * FROM items i WHERE i.user_id = :userId ORDER BY created_time DESC OFFSET :offset LIMIT " +
            ":limit", nativeQuery = true)
    List<Item> getAll(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);

    @Query("SELECT COUNT(*) FROM Item i WHERE i.user.id = :userId")
    int countAll(@Param("userId") Long userId);
}