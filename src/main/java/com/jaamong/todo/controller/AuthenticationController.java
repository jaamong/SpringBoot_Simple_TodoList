package com.jaamong.todo.controller;

import com.jaamong.todo.dto.*;
import com.jaamong.todo.entity.CustomUserDetails;
import com.jaamong.todo.jwt.JwtTokenUtils;
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

        TokenDto tokenDto = jwtTokenUtils.generateToken(user);
        jwtTokenUtils.checkAndUpdateRefreshToken(user.getUsername(), tokenDto);

        log.info("[login] user: {}, generated token: {}", user.toString(), tokenDto.getAccessToken());

        return ResponseEntity.ok(new LoginResponseDto(user, tokenDto));
    }

    /**
     * redis - blackList 등록
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/logout/{userId}")
    public void logout(@PathVariable("userId") Long userId, @RequestHeader("Authorization") String accessToken) {
        userService.logout(accessToken, userId);
        log.info("[logout] user: {} successfully logged out", userId);
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