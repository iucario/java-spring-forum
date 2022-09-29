package com.demo.app.repository;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String hashedPassword;
    @Column(name = "created_time", columnDefinition = "BIGINT")
    private Long createdAt;

    @OneToMany(mappedBy = "user")
    private Set<Item> items;

    @OneToMany(mappedBy = "user")
    private Set<Image> images;


    public User() {
    }

    public User(String name, String password) {
        this.name = name;
        this.hashedPassword = password;
        this.createdAt = new Date().getTime();
    }

    public static class UserCreate {
        public String name;
        public String password;

        public UserCreate(String name, String password) {
            this.name = name;
            this.password = password;
        }
    }

    @Override
    public String toString() {
        return String.format("User[id=%d, name='%s', created_time='%d']", id, name, createdAt);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
