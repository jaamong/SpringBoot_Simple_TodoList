package com.likelion.todo.dto;

import com.likelion.todo.entity.CustomUserDetails;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
