package com.likelion.todo;

import com.likelion.todo.model.TodoDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

@Service
public class TodoService {

    private static final List<TodoDto> store = new ArrayList<>();
    private static Long sequence = 0L;

    public TodoDto createTodo(String content) {
        TodoDto todoDto = new TodoDto(++sequence, content, false);
        store.add(todoDto);
        return todoDto;
    }

    public List<TodoDto> findAll() {
        return store;
    }

    public boolean updateTodoDone(Long id) {
        TodoDto todoDto = store.stream()
                .filter(o -> o.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (todoDto != null) {
            todoDto.setDone(!todoDto.getDone());
            return true;
        }
        return false;
    }

    public boolean deleteTodo(Long id) {
        OptionalInt findIdx = IntStream.range(0, store.size())
                .filter(i -> store.get(i).getId().equals(id))
                .findFirst();

        if (findIdx.isPresent()) {
            store.remove(findIdx.getAsInt());
            return true;
        }
        return false;
    }
}
