package com.sierkinroman.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.sierkinroman.entities.Role;
import com.sierkinroman.exception.ResourceAlreadyExistsException;
import com.sierkinroman.exception.ResourceNotFoundException;

public interface RoleService {
	
	Role findById(long id);
	
	Role findByName(String name);
	
	Set<Role> findAll();
	
	Role save(Role role);
	
	void update(Role role);
	
//	Role findById(long id) throws ResourceNotFoundException;
//	
//	Role findByName(String name) throws ResourceNotFoundException;
//	
//	Set<Role> findAll();
//	
//	Role save(Role role) throws ResourceAlreadyExistsException;
//	
//	void update(Role role) throws ResourceNotFoundException;
	
}
