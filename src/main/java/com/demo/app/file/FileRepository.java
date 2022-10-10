package com.demo.app.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileUpload, Long> {
    @Query("SELECT u from FileUpload u WHERE u.name = :name")
    Optional<FileUpload> findByName(@Param(value = "name") String name);

    @Query("SELECT u from FileUpload u WHERE u.user.id = :userId")
    Collection<FileUpload> findAllByUser(@Param(value = "userId") Long userId);
}
