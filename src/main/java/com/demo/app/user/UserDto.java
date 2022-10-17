package com.demo.app.user;

import org.springframework.lang.NonNull;

import javax.validation.constraints.Pattern;

public class UserDto {

    public String name;
    public Long createdAt;
    public int totalPosts;

    public UserDto(final String name, final Long createdAt, final int totalPosts) {
        this.name = name;
        this.createdAt = createdAt;
        this.totalPosts = totalPosts;
    }

    public static class UserLogin {
        public String name;
        public String password;
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
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$", message = "Password " +
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
