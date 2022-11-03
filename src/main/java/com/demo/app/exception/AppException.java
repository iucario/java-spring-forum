package com.demo.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AppException {
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "unauthorized")
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException() {
            super("Invalid username or password");
        }

        public UnauthorizedException(String message) {
            super(message);
        }
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "not found")
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "user not found")
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String name) {
            super("User not found for name : " + name);
        }

        public UserNotFoundException(Long id) {
            super("User not found for id : " + id);
        }
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "post not found")
    public static class PostNotFoundException extends RuntimeException {
        public PostNotFoundException(Long id) {
            super("Post not found for id : " + id);
        }
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "comment not found")
    public static class CommentNotFoundException extends RuntimeException {
        public CommentNotFoundException(Long id) {
            super("Comment not found for id : " + id);
        }
    }

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "user already exists")
    public static class UserExistsException extends RuntimeException {
        public UserExistsException(String name) {
            super("User already exists for name : " + name);
        }
    }
}
