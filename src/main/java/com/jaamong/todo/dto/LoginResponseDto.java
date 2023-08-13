package com.jaamong.todo.dto;

import com.jaamong.todo.entity.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDto {

    private CustomUserDetails user;
    private String token;

    @Override
    public String toString() {
        return "LoginResponseDto{" +
                "user=" + user +
                ", token='" + token + '\'' +
                '}';
    }
}
