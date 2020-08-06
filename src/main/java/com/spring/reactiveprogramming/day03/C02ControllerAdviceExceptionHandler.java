package com.spring.reactiveprogramming.day03;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * https://www.baeldung.com/global-error-handler-in-a-spring-rest-api
 *
 * 只有在spring-boot-starter-web被引入的情况下，才有用
 */
@ControllerAdvice
public class C02ControllerAdviceExceptionHandler {
    // public class C02ControllerAdviceExceptionHandler extends ResponseEntityExceptionHandler {
    @RequiredArgsConstructor
    @Data
    public static class InvalidField {
        private final String name;
        private final String message;
    }

    @RequiredArgsConstructor
    @Data
    public static class Error{
        private final List<InvalidField> invalidFields;
        private final List<String> errors;
    }

//    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
//                                                                  HttpHeaders httpHeaders,
//                                                                  HttpStatus httpStatus,
//                                                                  WebRequest request){
//        List<InvalidField> invalidFields = ex.getBindingResult().getFieldErrors().stream()
//                .map(error -> new InvalidField(error.getField(), error.getDefaultMessage()))
//                .collect(Collectors.toList());
//        List<String> errors = ex.getBindingResult().getGlobalErrors().stream()
//                .map(ObjectError::getDefaultMessage)
//                .collect(Collectors.toList());
//        Error error = new Error(invalidFields, errors);
//        return handleExceptionInternal(ex, error, headers, status, request);
//    }
//
//    protected ResponseEntity<Object> handleNotHandlerFoundException(NoHandlerFoundException ex,
//                                                                    HttpHeaders httpHeaders,
//                                                                    HttpStatus httpStatus,
//                                                                    WebRequest request){
//        Error error = new Error(Collections.emptyList(), Arrays.asList(
//           String.format("No handler for %s %s", ex.getHttpMethod(), ex.getRequestURL())
//        ));
//        return handleExceptionInternal(ex, error, headers, status, request);
//    }

}
