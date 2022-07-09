package com.sierkinroman.service;

import java.util.Set;

import org.springframework.data.domain.Page;

import com.sierkinroman.entities.User;

public interface UserService {
	
	User findById(long id);
	
	User findByUsername(String username);
	
	User findByEmail(String email);
	
	Set<User> findAll();
	
	Page<User> findPaginated(int pageNum, int pageSize);
	
	User save(User user);
	
	void update(User user);
	
	void deleteById(long id);

}
