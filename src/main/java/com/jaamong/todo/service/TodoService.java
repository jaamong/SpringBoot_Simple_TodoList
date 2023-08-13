package com.jaamong.todo.service;

import com.jaamong.todo.dto.TodoResponseDto;
import com.jaamong.todo.dto.TodoSaveRequestDto;
import com.jaamong.todo.dto.error.CustomErrorCode;
import com.jaamong.todo.entity.CustomUserDetails;
import com.jaamong.todo.repository.TodoRepository;
import com.jaamong.todo.repository.UserRepository;
import com.jaamong.todo.entity.Todo;
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

    public TodoResponseDto createTodo(TodoSaveRequestDto dto, Long userId) {

        CustomUserDetails user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(CustomErrorCode.NOT_FOUND_USER.name()));

        Todo todo = Todo.builder()
                .content(dto.getContent())
                .done(false)
                .user(user)
                .build();

        todoRepository.save(todo);

        return TodoResponseDto.from(todo);
    }

    public List<TodoResponseDto> findAll(Long id) {
        return todoRepository.findAllByUserId(id)
                .stream()
                .map(TodoResponseDto::from)
                .toList();
    }

    @Transactional
    public void updateTodoDone(Long userId, Long todoId) {
        Todo todo = getTodoById(userId, todoId);
        todo.updateDone(!todo.getDone());
        log.info("[updateTodoDone] done user[{}]'s status changed", userId);
    }

    @Transactional
    public void updateTodoContent(Long userId, Long todoId, String content) {
        Todo todo = getTodoById(userId, todoId);
        todo.updateContent(content);
        log.info("[updateTodoContent] done user[{}]'s content changed", userId);
    }

    public void deleteTodo(Long userId, Long todoId) {
        Todo todo = getTodoById(userId, todoId);
        todoRepository.delete(todo);
        log.info("[deleteTodo] delete user[{}]'s the todo : {}", userId, todoId);
    }

    private Todo getTodoById(Long userId, Long todoId) {
        return todoRepository.findAllByUserId(userId)
                .stream()
                .filter(t -> t.getId().equals(todoId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, CustomErrorCode.NOT_EXISTS_TODO.name())); //존재하지 않는 투두 입니다.
    }
}
