package com.Y_LAB.homework.in.controller;

import com.Y_LAB.homework.exception.ObjectNotFoundException;
import com.Y_LAB.homework.exception.user.auth.AuthorizeException;
import com.Y_LAB.homework.exception.user.auth.UserAlreadyExistsException;
import com.Y_LAB.homework.exception.validation.FieldNotValidException;
import com.Y_LAB.homework.model.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Класс для обработки исключений из REST Controller слоя
 * @author Денис Попов
 * @version 1.0
 */
@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {AuthorizeException.class})
    protected ResponseEntity<ErrorResponse> authorizeError(AuthorizeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(value = {UserAlreadyExistsException.class})
    protected ResponseEntity<ErrorResponse> userAlreadyExistsError(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(value = {FieldNotValidException.class})
    protected ResponseEntity<ErrorResponse> ObjectValidationError(FieldNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(value = {ObjectNotFoundException.class})
    protected ResponseEntity<ErrorResponse> ObjectNotFoundError(ObjectNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(value = {ClassCastException.class})
    protected ResponseEntity<ErrorResponse> notEnoughRightsError() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Not enough rights"));
    }
}