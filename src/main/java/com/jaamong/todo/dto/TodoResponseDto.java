package com.jaamong.todo.dto;

import com.jaamong.todo.entity.Todo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TodoResponseDto {

    private Long id;

    private String content;

    private Boolean done;

    public static TodoResponseDto from(Todo todo) {
        TodoResponseDto dto = new TodoResponseDto();
        dto.setId(todo.getId());
        dto.setContent(todo.getContent());
        dto.setDone(todo.getDone());
        return dto;
    }

    @Override
    public String toString() {
        return "TodoDto{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", done=" + done +
                '}';
    }
}
