package com.demo.app.exception;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity handleMethodArgumentTypeMismatchException(MethodArgumentNotValidException e) {
        List<String> message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        e.printStackTrace();
        if (e.getCause() instanceof UnrecognizedPropertyException) {
            UnrecognizedPropertyException cause = (UnrecognizedPropertyException) e.getCause();
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(List.of(cause.getPropertyName()));
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("invalid json");
    }

    private String createFieldErrorMessage(FieldError fieldError) {
        return "[" +
                fieldError.getField() +
                "] must be " +
                fieldError.getDefaultMessage() +
                ". your input: [" +
                fieldError.getRejectedValue() +
                "]";
    }
}
