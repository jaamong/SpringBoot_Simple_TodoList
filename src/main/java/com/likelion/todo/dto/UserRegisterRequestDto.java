package com.likelion.todo.dto;

import com.likelion.todo.entity.CustomUserDetails;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserRegisterRequestDto {

    @NotBlank(message = "enter your account's username")
    private String username;

    @Min(value = 8, message = "password must consisted of 8 characters at least")
    @NotBlank(message = "enter your account's password")
    private String password;

    @Email
    @NotBlank(message = "enter your account's email")
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
