package com.likelion.todo.repository;

import com.likelion.todo.entity.CustomUserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<CustomUserDetails, Long> {

    Optional<CustomUserDetails> findByUsername(String username);

    boolean existsByUsername(String username);
}
