package com.likelion.todo.service;

import com.likelion.todo.dto.UserLoginRequestDto;
import com.likelion.todo.entity.CustomUserDetails;
import com.likelion.todo.entity.Role;
import com.likelion.todo.repository.RoleRepository;
import com.likelion.todo.repository.UserRepository;
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
import java.util.Set;

import static com.likelion.todo.dto.error.CustomErrorCode.ALREADY_EXISTS_ID;
import static com.likelion.todo.dto.error.CustomErrorCode.NOT_FOUND_USER;

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
    private final PasswordEncoder encoder;

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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        return CustomUserDetails.builder()
                .id(loadUserIdByUsername(dto.getUsername()))
                .username(dto.getUsername())
                .password(dto.getPassword())
                .build();
    }

    private boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    private Long loadUserIdByUsername(String username) {
        CustomUserDetails user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(NOT_FOUND_USER.name()));
        return user.getId();
    }
}
