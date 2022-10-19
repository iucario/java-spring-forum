package com.demo.app.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    @Query("SELECT u from FileEntity u WHERE u.name = :name")
    Optional<FileEntity> findByName(@Param(value = "name") String name);

    @Query("SELECT u from FileEntity u WHERE u.user.id = :userId")
    List<FileEntity> findAllByUser(@Param(value = "userId") Long userId);
}
