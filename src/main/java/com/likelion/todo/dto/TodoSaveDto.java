package com.likelion.todo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TodoSaveDto {

    @NotBlank(message = "fill in the box")
    private String content;

    private Boolean done;
}
