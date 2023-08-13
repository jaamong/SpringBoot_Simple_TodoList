package com.jaamong.todo.dto;

import com.jaamong.todo.entity.CustomUserDetails;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserLoginRequestDto {

    private String username;
    private String password;

    public static UserRegisterRequestDto from(CustomUserDetails user) {
        UserRegisterRequestDto dto = new UserRegisterRequestDto();
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        return dto;
    }
}
