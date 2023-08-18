package com.jaamong.todo.service;

import com.jaamong.todo.dto.UserLoginRequestDto;
import com.jaamong.todo.entity.CustomUserDetails;
import com.jaamong.todo.entity.Role;
import com.jaamong.todo.jwt.JwtTokenUtils;
import com.jaamong.todo.redis.RedisUtil;
import com.jaamong.todo.repository.RefreshTokenRepository;
import com.jaamong.todo.repository.RoleRepository;
import com.jaamong.todo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.jaamong.todo.dto.error.CustomErrorCode.*;

/**
 * determines whether the user's username and password match up
 * implement UserDetailService
 * : authentication 동안 Spring Security 가 사용자를 찾는 방법을 지정
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository rtRepository;
    private final PasswordEncoder encoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final RedisUtil redisUtil;

    @Override
    public UserDetails loadUserByUsername(String username) {

        log.info("[loadUserByUsername] username : {}", username);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(NOT_FOUND_USER.name()));
    }

    @Transactional
    public void registerUser(String username, String password, String email) {
        String encodedPassword = encoder.encode(password);

        Set<Role> authorities = new HashSet<>();
        Role userRole = roleRepository.findByAuthority("USER").get();
        authorities.add(userRole);

        if (existsByUsername(username))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ALREADY_EXISTS_ID.name());

        userRepository.save(CustomUserDetails.builder()
                .username(username)
                .password(encodedPassword)
                .email(email)
                .authorities(authorities)
                .build());
    }

    public CustomUserDetails validateUser(UserLoginRequestDto dto) {

        //존재하는 사용자인지
        UserDetails userDetails = loadUserByUsername(dto.getUsername());

        //비밀번호 확인
        if (!encoder.matches(dto.getPassword(), userDetails.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, INVALID_PASSWORD.name());

        return CustomUserDetails.builder()
                .id(loadUserIdByUsername(dto.getUsername()))
                .username(dto.getUsername())
                .password(dto.getPassword())
                .build();
    }

    @Transactional
    public void logout(String accessToken, Long userId) {
        CustomUserDetails user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_FOUND_USER.name()));

        rtRepository.deleteRefreshTokenByUsername(user.getUsername()); //refresh token 삭제

        String token = accessToken.split(" ")[1];
        int expiration = (int) jwtTokenUtils.getExpiration(token); //access token 만료시간 조회

        redisUtil.setBlackList(token, "accessToken", expiration); //redis에 accessToken 사용 못하도록 등록
    }

    private boolean existsByUsername(String username) {
        log.info("check username [{}] if exists ", username);
        return userRepository.existsByUsername(username);
    }

    private Long loadUserIdByUsername(String username) {
        CustomUserDetails user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(NOT_FOUND_USER.name()));
        return user.getId();
    }
}
