package com.jaamong.todo.repository;

import com.jaamong.todo.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUsername(String username);
    void deleteRefreshTokenByUsername(String username);
}
