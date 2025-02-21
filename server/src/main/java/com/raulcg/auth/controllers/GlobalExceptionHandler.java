package com.raulcg.auth.controllers;

import com.raulcg.auth.response.GenericResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Manejo de excepciones generales
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse<String>> handleAllExceptions(Exception ex) {
        GenericResponse<String> response = new GenericResponse<>(null, ex.getMessage(), false);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Manejo de excepciones de validación de objetos (@Valid o @Validated en el RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericResponse<Map<String, String>>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((error) ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        GenericResponse<Map<String, String>> response = new GenericResponse<>(errors, "Errores de validación", false);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Manejo de excepciones de validación en parámetros (@Validated a nivel de método o de parámetros)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<GenericResponse<Map<String, String>>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(cv ->
                errors.put(cv.getPropertyPath().toString(), cv.getMessage())
        );
        GenericResponse<Map<String, String>> response = new GenericResponse<>(errors, "Violaciones de restricciones", false);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}