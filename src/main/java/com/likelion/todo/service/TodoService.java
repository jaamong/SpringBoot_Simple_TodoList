package com.likelion.todo.service;

import com.likelion.todo.entity.CustomUserDetails;
import com.likelion.todo.repository.TodoRepository;
import com.likelion.todo.entity.Todo;
import com.likelion.todo.repository.UserRepository;
import com.likelion.todo.dto.TodoSaveDto;
import com.likelion.todo.dto.TodoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoDto createTodo(TodoSaveDto form, String username) {

        CustomUserDetails user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        Todo todo = Todo.builder()
                .content(form.getContent())
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
    public void updateTodoDone(Long id) {
        Todo todo = getTodoById(id);
        todo.updateDone(!todo.getDone());
    }

    @Transactional
    public void updateTodoContent(Long id, String content) {
        Todo todo = getTodoById(id);
        todo.updateContent(content);
    }

    public void deleteTodo(Long id) {
        Todo todo = getTodoById(id);
        todoRepository.delete(todo);
    }

    private Todo getTodoById(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
