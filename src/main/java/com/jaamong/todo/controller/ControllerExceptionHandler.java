package com.jaamong.todo.controller;

import com.jaamong.todo.dto.error.ErrorMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    //입력 파라미터 검증 (not using)
    @ExceptionHandler
    public ResponseEntity<ErrorMessageDto> handleArgumentNotValidException(final MethodArgumentNotValidException e) {
        ErrorMessageDto message = new ErrorMessageDto(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        log.info("[MethodArgumentNotValidException] {}", message.getMessage());
        return ResponseEntity.status(e.getStatusCode()).body(message);
    }

    //ResponseStatusException
    @ExceptionHandler
    public ResponseEntity<ErrorMessageDto> handlerResponseStatusException(final ResponseStatusException e) {
        String split = e.getMessage().split("\\s")[2];
        ErrorMessageDto message = new ErrorMessageDto(split);
        log.info("[ResponseStatusException] {}", split);
        return ResponseEntity.status(e.getStatusCode()).body(message);
    }

    //UsernameNotFoundException
    @ExceptionHandler
    public ResponseEntity<ErrorMessageDto> handlerUsernameNotFoundException(final UsernameNotFoundException e) {
        ErrorMessageDto message = new ErrorMessageDto(e.getMessage());
        log.info("[UsernameNotFoundException] {}", message.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

}
