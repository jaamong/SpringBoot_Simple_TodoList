package com.likelion.todo.controller;

import com.likelion.todo.dto.LoginResponseDto;
import com.likelion.todo.entity.CustomUserDetails;
import com.likelion.todo.jwt.JwtTokenUtils;
import com.likelion.todo.dto.UserAuthRequestDto;
import com.likelion.todo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final PasswordEncoder encoder;

    @PostMapping("/register")
    public String registerUser(@Validated @RequestBody UserAuthRequestDto dto) {
        CustomUserDetails customUserDetails = userService.registerUser(dto.getUsername(), dto.getPassword(), dto.getEmail());
        return UserAuthRequestDto.from(customUserDetails).toString(); //나중에 FE 에서 사용자 정보 필요하면 toString() 제거
    }

    /**
     * jwt token 발급
     */
    @PostMapping("/login")
    public LoginResponseDto login(@Validated @RequestBody UserAuthRequestDto dto) {
        CustomUserDetails user = userService.validateUser(dto);
        String token = jwtTokenUtils.generateToken(user);
        return new LoginResponseDto(user, token);
    }

    /**
     * 인증이 필요한 URL
     */
    @PostMapping("/secured")
    public String checkSecure() {
        log.info(SecurityContextHolder.getContext()
                .getAuthentication()
                .getName());
        return "success";
    }
}
