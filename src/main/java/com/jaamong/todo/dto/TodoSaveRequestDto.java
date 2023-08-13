package com.jaamong.todo.dto;

import lombok.Getter;

@Getter
public class TodoSaveRequestDto {

    private String content;
    private Boolean done;
}
