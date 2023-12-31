package com.jaamong.todo.jwt;


import com.jaamong.todo.dto.TokenDto;
import com.jaamong.todo.dto.error.CustomErrorCode;
import com.jaamong.todo.entity.RefreshToken;
import com.jaamong.todo.redis.RedisUtil;
import com.jaamong.todo.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Component
public class JwtTokenUtils {

    private final Key signingKey;
    private final JwtParser jwtParser;
    private final RedisUtil redisUtil;
    private final RefreshTokenRepository rtRepository;


    private static final long ACCESS_TIME = 60 * 1000L;
    private static final long REFRESH_TIME = 2 * 60 * 1000L;

    public JwtTokenUtils(@Value("${jwt.secretKey}") String jwtSecret, RedisUtil redisUtil, RefreshTokenRepository rtRepository) {
        log.info("jwtSecret: {}", jwtSecret);

        this.redisUtil = redisUtil;
        this.rtRepository = rtRepository;

        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtParser = Jwts
                .parserBuilder()
                .setSigningKey(this.signingKey)
                .build();
    }

    /**
     * 주어진 사용자 정보를 바탕으로 JWT을 문자열로 생성
     */
    public TokenDto generateToken(UserDetails userDetails) {

        // --- Access Token ---
        Claims accessJwtClaims = Jwts.claims()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(ACCESS_TIME)));

        String accessToken = Jwts.builder()
                .setClaims(accessJwtClaims)
                .signWith(signingKey)
                .compact();

        // --- Refresh Token ---
        Claims refreshJwtClaims = Jwts.claims()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(REFRESH_TIME)));

        String refreshToken = Jwts.builder()
                .setClaims(refreshJwtClaims)
                .signWith(signingKey)
                .compact();

        //generate Token(AT+RT)
        return new TokenDto(accessToken, refreshToken);
    }

    @Transactional
    public void checkAndUpdateRefreshToken(String username, TokenDto token) {

        Optional<RefreshToken> refreshToken = rtRepository.findByUsername(username);

        //RT 있으면 새 토큰 발급 후 업데이트
        if (refreshToken.isPresent()) {
            rtRepository.save(refreshToken.get().updateToken(token.getRefreshToken()));
        } else { //없으면 재발급 후 DB에 저장
            RefreshToken newRT = new RefreshToken(token.getRefreshToken(), username);
            rtRepository.save(newRT);
        }
    }

    /**
     * JWT가 유효한지 판단하는 메소드
     * - JJWT 라이브러리에서는 JWT를 해석하는 과정에서 유효하지 않으면 예외 발생
     */
    public boolean validate(String token, String type) {

        try {
            //유효한 JWT -> true
            jwtParser.parseClaimsJws(token); //parseClaimsJws : 암호화된 JWT를 해석

            //access token이 redis에 있으면 이미 로그아웃 상태
            isLogout(token, type);

            return true;
        } catch (ExpiredJwtException e) {
            log.warn("[JwtTokenUtils - validate] expired JWT : {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.warn("[JwtTokenUtils - validate] unsupported JWT format/configuration : {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.warn("[JwtTokenUtils - validate] not correctly constructed JWT : {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            log.warn("[JwtTokenUtils - validate] calculating a signature or verifying an existing signature of a JWT failed : {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("[JwtTokenUtils - validate] JWT claims string is empty : {}", e.getMessage());
            return false;
        }
    }

    private void isLogout(String token, String type) {
        if (type.equals("AT") && redisUtil.hasKeyBlackList(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, CustomErrorCode.ALREADY_LOGOUT_USER.name());
        }
    }

    /**
     * JWT 토큰 만료시간
     */
    public long getExpiration(String accessToken) {
        Date expiration = jwtParser.parseClaimsJws(accessToken).getBody().getExpiration();
        long now = new Date().getTime();
        return expiration.getTime() - now;
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
