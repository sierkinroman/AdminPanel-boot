package com.sierkinroman.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sierkinroman.entities.User;
import com.sierkinroman.entities.repository.UserRepository;
import com.sierkinroman.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepo;
	
	@Override
	public User findById(long id) {
		User user = userRepo.findById(id).orElse(null);
		return user;
	}
	
	@Override
	public User findByUsername(String username) {
		User user = userRepo.findByUsername(username);
		return user;
	}

	@Override
	public User findByEmail(String email) {
		User user = userRepo.findByEmail(email);
		return user;
	}
	
	@Override
	public Set<User> findAll() {
		Set<User> users = new HashSet<>();
		userRepo.findAll().forEach(users::add);
		return users;
	}
	
	@Override
	public Page<User> findAll(Pageable pageable) {
		return userRepo.findAll(pageable);
	}

	@Override
	public User save(User user) {
		return userRepo.save(user);
	}
	
	@Override
	public void update(User user) {
		userRepo.save(user);	
	}

	@Override
	public void deleteById(long id) {
		userRepo.deleteById(id);
	}
	
}
