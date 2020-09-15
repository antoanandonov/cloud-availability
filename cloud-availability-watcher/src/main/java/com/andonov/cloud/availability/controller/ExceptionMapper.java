package com.andonov.cloud.availability.controller;

import com.andonov.cloud.availability.exception.ApiError;
import com.andonov.cloud.availability.exception.CmdExecutionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptionMapper extends ResponseEntityExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ApiError> handleTagSetNotFoundException(HttpClientErrorException e, HttpServletRequest request) {
        final HttpStatus httpStatus = e.getStatusCode();
        ApiError apiError = new ApiError(httpStatus, e.getMessage(), e.getResponseBodyAsString(), request.getRequestURL().toString());
        return ResponseEntity.status(httpStatus).body(apiError);
    }

    @ExceptionHandler(CmdExecutionFailedException.class)
    public ResponseEntity<ApiError> handleTagSetNotFoundException(CmdExecutionFailedException e, HttpServletRequest request) {
        final HttpStatus httpStatus = HttpStatus.EXPECTATION_FAILED;
        ApiError apiError = new ApiError(httpStatus, e.getMessage(), request.getRequestURL().toString());
        return ResponseEntity.status(httpStatus).body(apiError);
    }

}
