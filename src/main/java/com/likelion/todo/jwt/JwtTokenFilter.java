package com.likelion.todo.jwt;

import com.likelion.todo.entity.Role;
import com.likelion.todo.repository.RoleRepository;
import com.likelion.todo.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtils jwtTokenUtils;
    private final UserService userService;
    private final RoleRepository roleRepository;

    public JwtTokenFilter(JwtTokenUtils jwtTokenUtils, UserService userService, RoleRepository roleRepository) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("[doFilterInternal] start");

        //JWT가 포함되어 있으면 포함되어 있는 헤더 요청
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        //정상적인 인증 정보인지
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.split(" ")[1]; //Bearer 이후 토큰값 가져오기

            if (jwtTokenUtils.validate(token)) {

                log.info("[doFilterInternal] validate token : {}", token);

                String username = jwtTokenUtils.parseClaims(token).getSubject(); //JWT에서 subejct(사용자 이름/principal) 가져오기
                UserDetails userDetails = userService.loadUserByUsername(username);

                Set<Role> authorities = new HashSet<>();
                Role userRole = roleRepository.findByAuthority("USER").get();
                authorities.add(userRole);

                AbstractAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken( //사용자 인증 정보 생성
                        userDetails,
                        token, //비밀번호 (대신 token 으로 전달)
                        authorities //권한
                );

                SecurityContext context = SecurityContextHolder.createEmptyContext(); //새로운 Context 생성
                context.setAuthentication(authenticationToken); //SecurityContext에 사용자 정보 생성
                SecurityContextHolder.setContext(context); //SecurityContextHolder에 SecurityContext 설정

                log.info("[doFilterInternal] set security context with JWT");
            }
        } else
            log.warn("[doFilterInternal] JWT validation failed");

        filterChain.doFilter(request, response);
    }
}