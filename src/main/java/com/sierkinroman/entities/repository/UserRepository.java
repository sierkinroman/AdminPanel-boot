package com.sierkinroman.entities.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sierkinroman.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByUsername(String username);
	
	User findByEmail(String email);
	
}
