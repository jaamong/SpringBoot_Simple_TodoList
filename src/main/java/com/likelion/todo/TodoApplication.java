package com.likelion.todo;

import com.likelion.todo.entity.CustomUserDetails;
import com.likelion.todo.entity.Role;
import com.likelion.todo.repository.RoleRepository;
import com.likelion.todo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class TodoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoApplication.class, args);
	}

	@Bean
	CommandLineRunner run(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder encoder) {
		return args -> {

			if(roleRepository.findByAuthority("ADMIN").isPresent()) return;

			Role adminRole = roleRepository.save(Role.builder().authority("ADMIN").build());
			roleRepository.save(Role.builder().authority("USER").build());

			Set<Role> roles = new HashSet<>();
			roles.add(adminRole);

			CustomUserDetails admin = CustomUserDetails.builder()
					.id(1L)
					.username("admin")
					.password(encoder.encode("password"))
					.email("admin@gmail.com")
					.authorities(roles)
					.build();

			userRepository.save(admin);
		};
	}
}
