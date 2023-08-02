package com.likelion.todo.controller;

import com.likelion.todo.service.TodoService;
import com.likelion.todo.dto.TodoSaveDto;
import com.likelion.todo.dto.TodoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.HeadersBuilder;
import org.springframework.validation.annotation.Validated;
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
                                          @PathVariable("userId") Long userId, //안쓰는데 url에 있어서 받아온 것. todo : 나중에 Principal 대신 사용
                                          Principal principal) {

        log.info("TodoSaveDto : {}, {}", dto.getContent(), dto.getDone());
        TodoDto todo = todoService.createTodo(dto, principal.getName());
        log.info("todoDto : {}", todo.toString());

        return ResponseEntity.ok(todo);
    }

    @PutMapping("/{todoId}/done")
    public HeadersBuilder<?> updateDone(@PathVariable("userId") Long userId,
                                        @PathVariable("todoId") Long todoId,
                                        @RequestHeader("Authorization") String token) {
        todoService.updateTodoDone(userId, todoId);
        return ResponseEntity.noContent();
    }

    @PutMapping("/{todoId}/content")
    public HeadersBuilder<?> updateContent(@PathVariable("userId") Long userId,
                                           @PathVariable("todoId") Long todoId,
                                           @RequestBody TodoSaveDto dto,
                                           @RequestHeader("Authorization") String token) {

        todoService.updateTodoContent(userId, todoId, dto.getContent());
        return ResponseEntity.noContent();
    }

    @DeleteMapping("/{todoId}")
    public HeadersBuilder<?> delete(@PathVariable("userId") Long userId,
                                    @PathVariable("todoId") Long todoId,
                                    @RequestHeader("Authorization") String token) {
        todoService.deleteTodo(userId, todoId);
        return ResponseEntity.noContent();
    }
}
