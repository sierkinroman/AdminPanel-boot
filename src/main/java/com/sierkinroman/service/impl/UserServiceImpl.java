package com.sierkinroman.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sierkinroman.entities.User;
import com.sierkinroman.entities.repository.UserRepository;
import com.sierkinroman.exception.ResourceNotFoundException;
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
		//TODO maybe change to SortedHashSet, ordered collection
		Set<User> users = new HashSet<>();
		userRepo.findAll().forEach(users::add);
		return users;
	}

	@Override
	public User save(User user) {
		return userRepo.save(user);
	}
	
	@Override
	public void update(User user) throws ResourceNotFoundException {
		userRepo.save(user);	
	}

	@Override
	public void deleteById(long id) throws ResourceNotFoundException {
		userRepo.deleteById(id);
	}
	
//	@Override
//	public User findById(long id) throws ResourceNotFoundException {
//		User user = userRepo.findById(id).orElse(null);
//		if (user == null) {
//			throw new ResourceNotFoundException("Cannot find User with id: '" + id + "'");
//		}
//		return user;
//	}
//	
//	@Override
//	public User findByUsername(String username) throws ResourceNotFoundException {
//		User user = userRepo.findByUsername(username);
//		if (user == null) {
//			throw new ResourceNotFoundException("Cannot find User with username: '" + username + "'");
//		}
//		return user;
//	}
//
//	@Override
//	public User findByEmail(String email) throws ResourceNotFoundException {
//		User user = userRepo.findByEmail(email);
//		if (user == null) {
//			throw new ResourceNotFoundException("Cannot find User with email: '" + email + "'");
//		}
//		return user;
//	}
//	
//	@Override
//	public Set<User> findAll() {
//		//TODO maybe change to SortedHashSet, ordered collection
//		Set<User> users = new HashSet<>();
//		userRepo.findAll().forEach(users::add);
//		return users;
//	}
//
//	@Override
//	public User save(User user) {
//		return userRepo.save(user);
//	}
//	
//	@Override
//	public void update(User user) throws ResourceNotFoundException {
//		if (existsById(user.getId())) {
//			userRepo.save(user);
//		}
//		throw new ResourceNotFoundException("Cannot find User with id: '" + user.getId() + "'");
//		
//	}
//
//	@Override
//	public void deleteById(long id) throws ResourceNotFoundException {
//		if (existsById(id)) {
//			userRepo.deleteById(id);
//		}
//		throw new ResourceNotFoundException("Cannot find User with id: '" + id + "'");
//		
//	}
	
	private boolean existsById(long id) {
		return userRepo.existsById(id);
	}
	
}
