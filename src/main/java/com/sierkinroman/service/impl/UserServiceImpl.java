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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepo;
	
	@Override
	public User findById(long id) {
		log.debug("Find User by id '{}'", id);
		return userRepo.findById(id).orElse(null);
	}
	
	@Override
	public User findByUsername(String username) {
		log.debug("Find User by username '{}'", username);
		return userRepo.findByUsername(username);
	}

	@Override
	public User findByEmail(String email) {
		log.debug("Find User by email '{}'", email);
		return userRepo.findByEmail(email);
	}
	
	@Override
	public Set<User> findAll() {
		log.debug("Find all Users");
		Set<User> users = new HashSet<>();
		userRepo.findAll().forEach(users::add);
		return users;
	}
	
	@Override
	public Page<User> findAll(Pageable pageable) {
		log.debug("Find all Users with pageable {}", pageable);
		return userRepo.findAllDistinctBy(pageable);
	}

	@Override
	public User save(User user) {
		log.debug("Save User with id '{}'", user.getId());
		return userRepo.save(user);
	}
	
	@Override
	public void update(User user) {
		log.debug("Update User with id '{}'", user.getId());
		userRepo.save(user);	
	}

	@Override
	public void deleteById(long id) {
		log.debug("Delete User with id - '{}'", id);
		userRepo.deleteById(id);
	}
	
}
