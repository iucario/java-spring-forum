package com.demo.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AppException {
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "unauthorized")
    public static class UnauthorizedException extends RuntimeException {
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

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "user already exists")
    public static class UserExistsException extends RuntimeException {
        public UserExistsException(String message) {
            super(message);
        }
    }
}
