package com.yakubovskiy.project.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(value = LogicException.class)
    public ResponseEntity<Map<String,String>> handleLogicExceptions(LogicException e) {
        log.error("Logic error occurred");
        Map<String,String> errors = new HashMap<>();
        errors.put("Error",e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFoundExceptions(ResourceNotFoundException e) {
        log.error("Not found exception");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

}
