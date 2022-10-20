package com.demo.app.file;

import com.demo.app.user.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "files")
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_seq")
    private Long id;
    private String name;
    private String url;
    @Column(name = "created_at", columnDefinition = "BIGINT")
    private Long createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public FileEntity() {
    }

    public FileEntity(String name, String url, User user) {
        this.name = name;
        this.url = url;
        this.user = user;
        this.createdAt = new Date().getTime();
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public Long getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUserId() {
        return user.getId();
    }
}
