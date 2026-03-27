package com.hostel.exceptions;


import com.hostel.web.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExists(ResourceAlreadyExistsException ex, HttpServletRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                                            .errorMsg(ex.getMessage())
                                            .status(HttpStatus.CONFLICT.value())
                                            .timeStamp(LocalDateTime.now())
                                            .path(request.getRequestURI())
                                            .method(request.getMethod())
                                            .build();


        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);

    }

    @ExceptionHandler(Exception.class)
    public String globalExecption(Exception ex) {

        return "something is fishy :"+ex.getMessage();

    }


}