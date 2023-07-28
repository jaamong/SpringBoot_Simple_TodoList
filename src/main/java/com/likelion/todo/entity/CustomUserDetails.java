package com.likelion.todo.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * information about the user (similar DTO)
 */
@Entity
@Table(name = "users")
public class CustomUserDetails implements UserDetails {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Getter
    @Column(nullable = false)
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role_junction",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private Set<Role> authorities;

    @Getter
    @OneToMany(mappedBy = "user")
    private List<Todo> todos = new ArrayList<>();

    public CustomUserDetails() {
        super();
        this.authorities = new HashSet<>();
    }

    @Builder
    public CustomUserDetails(Long id, String username, String password, String email, Todo todo, Set<Role> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.authorities = authorities;

        if (todo != null) {
            todo.setUser(this);
            this.todos.add(todo);
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    /**
     * false : the accounts can be locked down
     * true : the accounts is usable
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 반환 값 : isAccountNonExpired() 와 동일한 의미
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateAuthorities(Set<Role> authorities) {
        this.authorities = authorities;
    }
}
