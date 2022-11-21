package com.demo.app.user;

import com.demo.app.user.userStats.UserStats;
import org.springframework.lang.NonNull;

import javax.validation.constraints.Pattern;

public class UserDto {

    public Long id;
    public String name;
    public Long createdAt;
    public Long postCount;
    public Long commentCount;
    public Long fileCount;

    public UserDto() {
    }

    public UserDto(final User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.createdAt = user.getCreatedAt();
        this.postCount = user.getUserStats().getPostCount();
        this.commentCount = user.getUserStats().getCommentCount();
        this.fileCount = user.getUserStats().getFileCount();
    }

    public UserDto(final User user, final UserStats userStats) {
        this.id = user.getId();
        this.name = user.getName();
        this.createdAt = user.getCreatedAt();
        this.postCount = userStats.getPostCount();
        this.commentCount = userStats.getCommentCount();
        this.fileCount = userStats.getFileCount();
    }

    public static class UserLogin {
        public String name;
        public String password;

        public UserLogin(final String name, final String password) {
            this.name = name;
            this.password = password;
        }
    }

    public static class LoginResponse {
        public String token;

        public LoginResponse(final String token) {
            this.token = token;
        }
    }

    public static class UserCreate {
        @NonNull()
        @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "Username must be 3-20 characters long and contain only " +
                "letters, numbers and underscores")
        public String name;
        @NonNull
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!()])(?=\\S+$).{8,}$", message = "Password" +
                " " +
                "must contain at least one digit, one lowercase letter, one uppercase letter, one special character " +
                "in @#$%^&+=! and must be at least 8 characters long")
        public String password;

        public UserCreate(String name, String password) {
            this.name = name;
            this.password = password;
        }

        @Override
        public String toString() {
            return "UserCreate[name=" + name + ", password=" + password + "]";
        }
    }
}
