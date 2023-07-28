package com.likelion.todo.dto;

import com.likelion.todo.entity.Todo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TodoDto {

    private Long id;

    private String content;

    private Boolean done;

    public static TodoDto from(Todo todo) {
        TodoDto dto = new TodoDto();
        dto.setId(todo.getId());
        dto.setContent(todo.getContent());
        dto.setDone(todo.getDone());
        return dto;
    }
}
