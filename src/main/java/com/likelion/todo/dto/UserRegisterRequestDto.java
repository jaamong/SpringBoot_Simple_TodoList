package com.likelion.todo.dto;

import com.likelion.todo.entity.CustomUserDetails;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserRegisterRequestDto {

    private String username;
    private String password;
    @Email
    private String email;

    public static UserRegisterRequestDto from(CustomUserDetails user) {
        UserRegisterRequestDto dto = new UserRegisterRequestDto();
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setEmail(user.getEmail());
        return dto;
    }

    @Override
    public String toString() {
        return String.format("<Registration Info>\n username: %s\n password: [PROTECTED]\n email: %s\n", username, email);
    }
}
