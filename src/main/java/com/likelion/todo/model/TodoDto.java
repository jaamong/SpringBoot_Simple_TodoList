package com.likelion.todo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@AllArgsConstructor
@ToString
public class TodoDto {

    private Long id;
    private String content;
    private Boolean done;

}
