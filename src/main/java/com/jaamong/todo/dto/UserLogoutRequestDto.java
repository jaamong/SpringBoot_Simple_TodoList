package com.jaamong.todo.dto;

import lombok.Getter;

@Getter
public class UserLogoutRequestDto {

    private Long userId;
    private String accessToken;
}
