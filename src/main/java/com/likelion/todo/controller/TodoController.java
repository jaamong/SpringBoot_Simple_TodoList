package com.likelion.todo.controller;

import com.likelion.todo.dto.TodoDto;
import com.likelion.todo.dto.TodoSaveDto;
import com.likelion.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public ResponseEntity<List<TodoDto>> readAll(@PathVariable("userId") Long userId,
                                                 @RequestHeader("Authorization") String token) {

        log.info("[readAll] jwt token : {}", token);
        List<TodoDto> todoList = todoService.findAll(userId);
        log.info(todoList.toString());

        return ResponseEntity.ok(todoList);
    }

    @PostMapping
    public ResponseEntity<TodoDto> create(@RequestHeader("Authorization") String token,
                                          @RequestBody TodoSaveDto dto,
                                          @PathVariable("userId") Long userId) {

        log.info("TodoSaveDto : {}, {}", dto.getContent(), dto.getDone());
        TodoDto todo = todoService.createTodo(dto, userId);
        log.info("todoDto : {}", todo.toString());

        return ResponseEntity.ok(todo);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{todoId}/done")
    public void updateDone(@PathVariable("userId") Long userId,
                           @PathVariable("todoId") Long todoId,
                           @RequestHeader("Authorization") String token) {

        todoService.updateTodoDone(userId, todoId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{todoId}/content")
    public void updateContent(@PathVariable("userId") Long userId,
                              @PathVariable("todoId") Long todoId,
                              @RequestBody String content,
                              @RequestHeader("Authorization") String token) {

        todoService.updateTodoContent(userId, todoId, content);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{todoId}")
    public void delete(@PathVariable("userId") Long userId,
                       @PathVariable("todoId") Long todoId,
                       @RequestHeader("Authorization") String token) {

        todoService.deleteTodo(userId, todoId);
    }
}
