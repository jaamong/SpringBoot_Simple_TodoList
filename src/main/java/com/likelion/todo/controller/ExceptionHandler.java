package com.likelion.todo.controller;

import com.likelion.todo.dto.error.ErrorMessageDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler {

    //입력 파라미터 검증
    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorMessageDto> handleArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorMessageDto message = new ErrorMessageDto(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return ResponseEntity.status(e.getStatusCode()).body(message);
    }
}
