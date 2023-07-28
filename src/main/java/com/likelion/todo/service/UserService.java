package com.likelion.todo.service;

import com.likelion.todo.dto.UserAuthRequestDto;
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("In the user details service");

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " is not valid"));
    }

    @Transactional
    public CustomUserDetails registerUser(String username, String password, String email) {
        String encodedPassword = encoder.encode(password);

        Set<Role> authorities = new HashSet<>();
        Role userRole = roleRepository.findByAuthority("USER").get();
        authorities.add(userRole);

        if (existsByUsername(username))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임입니다.");

        return userRepository.save(CustomUserDetails.builder()
                .username(username)
                .password(encodedPassword)
                .email(email)
                .build());
    }

    public CustomUserDetails validateUser(UserAuthRequestDto dto) {

        //존재하는 사용자인지
        UserDetails userDetails = loadUserByUsername(dto.getUsername());

        //비밀번호 확인
        if (!encoder.matches(dto.getPassword(), userDetails.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return CustomUserDetails.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .build();
    }

    private boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}