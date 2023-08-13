package com.jaamong.todo.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenUtils {

    private final Key signingKey;
    private final JwtParser jwtParser;

    public JwtTokenUtils(@Value("${jwt.secretKey}") String jwtSecret) {
        log.info("jwtSecret: {}", jwtSecret);

        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtParser = Jwts
                .parserBuilder()
                .setSigningKey(this.signingKey)
                .build();
    }

    /**
     * 주어진 사용자 정보를 바탕으로 JWT을 문자열로 생성
     */
    public String generateToken(UserDetails userDetails) {

        Claims jwtClaims = Jwts.claims()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(3600)));

        return Jwts.builder()
                .setClaims(jwtClaims)
                .signWith(signingKey)
                .compact();
    }

    /**
     * JWT가 유효한지 판단하는 메소드
     * - JJWT 라이브러리에서는 JWT를 해석하는 과정에서 유효하지 않으면 예외 발생
     */
    public boolean validate(String token) {

        try {
            //유효한 JWT -> true
            jwtParser.parseClaimsJws(token); //parseClaimsJws : 암호화된 JWT를 해석
            return true;
        } catch (Exception e) {
            log.warn("[JwtTokenUtils] invalid JWT in validate()");
            return false;
        }
    }

    /**
     * JWT를 인자로 받고, 그 JWT를 해석하여 사용자 정보를 회수
     */
    public Claims parseClaims(String token) {
        return jwtParser
                .parseClaimsJws(token)
                .getBody();
    }
}
