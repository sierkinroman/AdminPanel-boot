package com.sierkinroman.service;

import java.util.Set;

import com.sierkinroman.entities.Role;

public interface RoleService {
	
	Role findById(long id);
	
	Role findByName(String name);
	
	Set<Role> findAll();
	
	Role save(Role role);
	
	void update(Role role);
	
}
