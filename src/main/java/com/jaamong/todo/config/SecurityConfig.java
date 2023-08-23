package com.jaamong.todo.config;

import com.jaamong.todo.jwt.JwtExceptionHandlerFilter;
import com.jaamong.todo.jwt.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;
    private final JwtExceptionHandlerFilter jwtExceptionHandlerFilter;

    public SecurityConfig(JwtTokenFilter jwtTokenFilter, JwtExceptionHandlerFilter jwtExceptionHandlerFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.jwtExceptionHandlerFilter = jwtExceptionHandlerFilter;
    }

    /**
     * <로그아웃>
     * # 로그인 -> 쿠키를 통해 세션을 생성 (아이디, 비밀번호)
     * # 로그아웃 -> 세션 제거 (세션 정보만 있으면 제거 가능)
     */

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/todo-home", "/todo-list", "auth/login", "/register.html").permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtTokenFilter, AuthorizationFilter.class)
                .addFilterBefore(jwtExceptionHandlerFilter, JwtTokenFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustom() throws Exception {
        return (webSecurity) -> webSecurity
                .ignoring()
                .requestMatchers("/static/**")
                .anyRequest();
    }

    //AuthenticationManager : 어떻게 사용자를 인증할 건지 정의 -> 구현한 UserDetailsService 사용
    //ProviderManager : AuthenticationManager 구현체
    // - ProviderManager 는 AuthenticationProvider 목록을 위임 받는다
    // - 이로써 같은 애플리케이션 내에 서로 다른 인증 매커니즘 지원 가능
    // - AuthenticationProvider : 인증 절차 정의, DB 에서 가져온 정보와 로그인 정보를 비교
    //      - DaoAuthenticationProvider : AuthenticationProvider 구현체
    //            - UserDetailsService 및 PasswordEncoder 를 활용하여 사용자 이름과 암호를 인증
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(userDetailsService);
        return new ProviderManager(daoProvider);
    }
}
