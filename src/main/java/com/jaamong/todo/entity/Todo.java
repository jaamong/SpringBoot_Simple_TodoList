package com.jaamong.todo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Todo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    private Boolean done;

    @ManyToOne
    private CustomUserDetails user;

    @Builder
    public Todo(Long id, String content, Boolean done, CustomUserDetails user) {
        this.id = id;
        this.content = content;
        this.done = done;
        this.user = user;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateDone(Boolean done) {
        this.done = done;
    }

    public void setUser(CustomUserDetails user) {
        this.user = user;
    }
}
