package com.jaamong.todo.jwt;

import com.jaamong.todo.entity.RefreshToken;
import com.jaamong.todo.entity.Role;
import com.jaamong.todo.repository.RefreshTokenRepository;
import com.jaamong.todo.repository.RoleRepository;
import com.jaamong.todo.service.UserService;
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
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtils jwtTokenUtils;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtTokenFilter(JwtTokenUtils jwtTokenUtils, UserService userService, RoleRepository roleRepository, RefreshTokenRepository refreshTokenRepository) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("[doFilterInternal] start");

        //JWT가 포함되어 있으면 포함되어 있는 헤더 요청
        String accessHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String refreshHeader = request.getHeader("Authorization-Expiration");

        //정상적인 인증 정보인지
        if (accessHeader != null && accessHeader.startsWith("Bearer ")) {

            String accessToken = accessHeader.split(" ")[1]; //Bearer 이후 토큰값 가져오기

            if (jwtTokenUtils.validate(accessToken, "AT")) { //AT 유효

                log.info("[doFilterInternal] validate accessToken : {}", accessToken);

                String username = jwtTokenUtils.parseClaims(accessToken).getSubject(); //JWT에서 subejct(사용자 이름/principal) 가져오기
                UserDetails userDetails = userService.loadUserByUsername(username);
                Set<Role> authorities = getRoles("USER");

                AbstractAuthenticationToken authenticationToken = getAuthenticationToken(userDetails, accessToken, authorities);
                setAuthentication(authenticationToken);

                log.info("[doFilterInternal] Access Token : set security context with JWT");

            } else if (refreshHeader != null && refreshHeader.startsWith("ExpRTkn")) { //AT 만료 && RT 존재

                String refreshToken = refreshHeader.split(" ")[1];

                if (jwtTokenUtils.validate(refreshToken, "RT")) { //RT 유효

                    // DB에 저장된 토큰과 일치하는지
                    String username = jwtTokenUtils.parseClaims(accessToken).getSubject(); //JWT에서 subejct(사용자 이름/principal) 가져오기
                    Optional<RefreshToken> OptRT = refreshTokenRepository.findByUsername(username);
                    boolean isEqual = OptRT.isPresent() && refreshToken.equals(OptRT.get());

                    if (isEqual) { //RT 일치

                        log.info("[doFilterInternal] validate Refresh Token : {}", refreshToken);

                        UserDetails userDetails = userService.loadUserByUsername(username);
                        String newAT = jwtTokenUtils.generateToken(userDetails).getAccessToken();
                        Set<Role> authorities = getRoles("USER");

                        AbstractAuthenticationToken authenticationToken = getAuthenticationToken(userDetails, newAT, authorities);
                        setAuthentication(authenticationToken);

                        log.info("[doFilterInternal] Access Token : set security context with new JWT");

                    } else { //RT 만료 || RT != DB.RT
                        log.warn("[doFilterInternal] Refresh Token : JWT expired || JWT validation failed ");
                    }
                }
            }
        } else
            log.warn("[doFilterInternal] JWT validation failed");

        filterChain.doFilter(request, response);
    }

    //사용자 인증 정보 생성
    private AbstractAuthenticationToken getAuthenticationToken(UserDetails userDetails, String newAT, Set<Role> authorities) {
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                newAT, //비밀번호 (대신 accessToken 으로 전달)
                authorities //권한
        );
    }

    // Security Context에 인증 정보 설정
    private void setAuthentication(AbstractAuthenticationToken authenticationToken) {
        SecurityContext context = SecurityContextHolder.createEmptyContext(); //새로운 Context 생성
        context.setAuthentication(authenticationToken); //SecurityContext에 사용자 정보 생성
        SecurityContextHolder.setContext(context); //SecurityContextHolder에 SecurityContext 설정
    }

    private Set<Role> getRoles(String authority) {
        Set<Role> authorities = new HashSet<>();
        Role userRole = roleRepository.findByAuthority(authority).get();
        authorities.add(userRole);
        return authorities;
    }
}