package com.likelion.todo.service;

import com.likelion.todo.dto.TodoDto;
import com.likelion.todo.dto.TodoSaveDto;
import com.likelion.todo.entity.CustomUserDetails;
import com.likelion.todo.entity.Todo;
import com.likelion.todo.repository.TodoRepository;
import com.likelion.todo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoDto createTodo(TodoSaveDto dto, Long userId) {

        CustomUserDetails user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Not Found User"));

        Todo todo = Todo.builder()
                .content(dto.getContent())
                .done(false)
                .user(user)
                .build();

        todoRepository.save(todo);

        return TodoDto.from(todo);
    }

    public List<TodoDto> findAll(Long id) {
        return todoRepository.findAllByUserId(id)
                .stream()
                .map(TodoDto::from)
                .toList();
    }

    @Transactional
    public void updateTodoDone(Long userId, Long todoId) {
        Todo todo = getTodoById(userId, todoId);
        todo.updateDone(!todo.getDone());
        log.info("[updateTodoDone] done status changed to {}", getTodoById(userId, todoId).getDone());
    }

    @Transactional
    public void updateTodoContent(Long userId, Long todoId, String content) {
        Todo todo = getTodoById(userId, todoId);
        todo.updateContent(content);
        log.info("[updateTodoContent] done content changed to {}", getTodoById(userId, todoId).getContent());
    }

    public void deleteTodo(Long userId, Long todoId) {
        Todo todo = getTodoById(userId, todoId);
        todoRepository.delete(todo);
    }

    private Todo getTodoById(Long userId, Long todoId) {
        return todoRepository.findAllByUserId(userId)
                .stream()
                .filter(t -> t.getId().equals(todoId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
