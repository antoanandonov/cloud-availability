package com.andonov.cloud.availability.controller;

import com.andonov.cloud.availability.exception.ApiError;
import com.andonov.cloud.availability.exception.CallbackAlreadyExistsException;
import com.andonov.cloud.availability.exception.CallbackNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice(assignableTypes = ServiceController.class)
public class ServiceControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CallbackNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(CallbackNotFoundException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e, request);
    }

    @ExceptionHandler(CallbackAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleAlreadyExistException(CallbackAlreadyExistsException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, e, request);
    }

    private ResponseEntity<ApiError> buildErrorResponse(HttpStatus httpStatus, Exception e, HttpServletRequest request) {
        ApiError apiError = new ApiError(httpStatus, e.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(apiError, httpStatus);
    }

}
