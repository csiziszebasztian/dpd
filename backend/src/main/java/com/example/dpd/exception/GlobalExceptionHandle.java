package com.example.dpd.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

@RestControllerAdvice
public class GlobalExceptionHandle extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public Problem handleResourceNotFoundException(ResourceNotFoundException exception) {
        return Problem.builder()
                .withStatus(Status.NOT_FOUND)
                .withTitle("User not found")
                .withDetail(exception.getMessage())
                .build();
    }

}
