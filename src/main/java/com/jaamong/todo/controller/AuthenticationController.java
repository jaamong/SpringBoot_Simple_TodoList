package com.jaamong.todo.controller;

import com.jaamong.todo.dto.LoginResponseDto;
import com.jaamong.todo.dto.UserLoginRequestDto;
import com.jaamong.todo.dto.UserRegisterRequestDto;
import com.jaamong.todo.jwt.JwtTokenUtils;
import com.jaamong.todo.entity.CustomUserDetails;
import com.jaamong.todo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final PasswordEncoder encoder;

    /**
     * 회원 가입
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/register")
    public void registerUser(@RequestBody UserRegisterRequestDto dto) {
        userService.registerUser(dto.getUsername(), dto.getPassword(), dto.getEmail());
    }

    /**
     * jwt token 발급
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody UserLoginRequestDto dto) {
        CustomUserDetails user = userService.validateUser(dto);
        String token = jwtTokenUtils.generateToken(user);

        log.info("[login] user: {}, generated token: {}", user.toString(), token);

        return ResponseEntity.ok(new LoginResponseDto(user, token));
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