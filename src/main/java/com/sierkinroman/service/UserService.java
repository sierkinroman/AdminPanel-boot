package com.sierkinroman.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.sierkinroman.entities.User;
import com.sierkinroman.exception.ResourceNotFoundException;

public interface UserService {
	
	User findById(long id);
	
	User findByUsername(String username);
	
	User findByEmail(String email);
	
	Set<User> findAll();
	
	User save(User user);
	
	void update(User user);
	
	void deleteById(long id);

//	User findById(long id) throws ResourceNotFoundException;
//	
//	User findByUsername(String username) throws ResourceNotFoundException;
//	
//	User findByEmail(String email) throws ResourceNotFoundException;
//	
//	Set<User> findAll();
//	
//	User save(User user);
//	
//	void update(User user) throws ResourceNotFoundException;
//	
//	void deleteById(long id) throws ResourceNotFoundException;
}
