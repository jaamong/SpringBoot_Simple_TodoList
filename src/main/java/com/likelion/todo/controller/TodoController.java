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
@RequestMapping("/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping("/{id}")
    public ResponseEntity<List<TodoDto>> readAll(@PathVariable("id") Long id,
                                                 @RequestHeader("Authorization") String token) {

        log.info("[readAll] jwt token : {}", token);
        List<TodoDto> todoList = todoService.findAll(id);
        log.info(todoList.toString());

        return ResponseEntity.ok(todoList);
    }

    @PostMapping
    public ResponseEntity<TodoDto> create(@RequestHeader("Authorization") String token,
                                          @RequestBody TodoSaveDto dto,
                                          Principal principal) {

        log.info("TodoSaveDto : {}, {}", dto.getContent(), dto.getDone());
        TodoDto todo = todoService.createTodo(dto, principal.getName());
        log.info("todoDto : {}", todo.toString());

        return ResponseEntity.ok(todo);
    }

    @PutMapping("/{id}/done")
    public HeadersBuilder<?> updateDone(@PathVariable("id") Long id,
                                        @RequestHeader("Authorization") String token) {
        todoService.updateTodoDone(id);
        return ResponseEntity.noContent();
    }

    @PutMapping("/{id}/content")
    public HeadersBuilder<?> updateContent(@PathVariable("id") Long id,
                                           @RequestBody TodoSaveDto dto,
                                           @RequestHeader("Authorization") String token) {

        todoService.updateTodoContent(id, dto.getContent());
        return ResponseEntity.noContent();
    }

    @DeleteMapping("/{id}")
    public HeadersBuilder<?> delete(@PathVariable("id") Long id,
                                    @RequestHeader("Authorization") String token) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent();
    }
}
