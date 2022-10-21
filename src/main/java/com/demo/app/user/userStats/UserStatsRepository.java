package com.demo.app.user.userStats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStats, Long> {
    @Query("SELECT u from UserStats u WHERE u.user.id = :userId")
    Optional<UserStats> findByUserId(@Param(value = "userId") Long userId);

}
