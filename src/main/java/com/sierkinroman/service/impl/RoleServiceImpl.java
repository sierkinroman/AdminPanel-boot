package com.sierkinroman.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sierkinroman.entities.Role;
import com.sierkinroman.entities.repository.RoleRepository;
import com.sierkinroman.service.RoleService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleRepository roleRepo;
	
	@Override
	public Role findById(long id) {
		log.debug("Find Role by id '{}'", id);
		Role role = roleRepo.findById(id).orElse(null);
		return role;
	}

	@Override
	public Role findByName(String name) {
		log.debug("Find Role by name '{}'", name);
		Role role = roleRepo.findByName(name);
		return role;
	}

	@Override
	public Set<Role> findAll() {
		log.debug("Find all Roles");
		Set<Role> roles = new HashSet<>();
		roleRepo.findAll().forEach(roles::add);
		return roles;
	}

	@Override
	public Role save(Role role) {
		log.debug("Save Role with id '{}'", role.getId());
		return roleRepo.save(role);
	}

	@Override
	public void update(Role role) {
		log.debug("Update Role with id '{}'", role.getId());
		roleRepo.save(role);
	}

}
