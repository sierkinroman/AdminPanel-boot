package com.sierkinroman.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sierkinroman.entities.Role;
import com.sierkinroman.entities.repository.RoleRepository;
import com.sierkinroman.exception.ResourceAlreadyExistsException;
import com.sierkinroman.exception.ResourceNotFoundException;
import com.sierkinroman.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleRepository roleRepo;
	
	@Override
	public Role findById(long id) {
		Role role = roleRepo.findById(id).orElse(null);
		return role;
	}

	@Override
	public Role findByName(String name) {
		Role role = roleRepo.findByName(name);
		return role;
	}

	@Override
	public Set<Role> findAll() {
		// TODO maybe change to SortedHashSet, ordered collection
		Set<Role> roles = new HashSet<>();
		roleRepo.findAll().forEach(roles::add);
		return roles;
	}

	@Override
	public Role save(Role role) {
		return roleRepo.save(role);
	}

	@Override
	public void update(Role role) {
		roleRepo.save(role);
	}
	
//	@Override
//	public Role findById(long id) throws ResourceNotFoundException {
//		Role role = roleRepo.findById(id).orElse(null);
//		if (role == null) {
//			throw new ResourceNotFoundException("Cannot find Role with id: '" + id + "'");
//		}
//		return role;
//	}
//
//	@Override
//	public Role findByName(String name) throws ResourceNotFoundException {
//		Role role = roleRepo.findByName(name);
//		if (role == null) {
//			throw new ResourceNotFoundException("Cannot find Role with name: '" + name + "'");
//		}
//		return role;
//	}
//
//	@Override
//	public Set<Role> findAll() {
//		// TODO maybe change to SortedHashSet, ordered collection
//		Set<Role> roles = new HashSet<>();
//		roleRepo.findAll().forEach(roles::add);
//		return roles;
//	}
//
//	@Override
//	public Role save(Role role) throws ResourceAlreadyExistsException {
//		if (findByName(role.getName()) != null) {
//			throw new ResourceAlreadyExistsException("Role with name: '" + role.getName() + "' already exists");
//		}
//		return roleRepo.save(role);
//	}
//
//	@Override
//	public void update(Role role) throws ResourceNotFoundException {
//		if (existsById(role.getId())) {
//			roleRepo.save(role);
//		}
//		throw new ResourceNotFoundException("Cannot find Role with id: '" + role.getId() + "'");
//	}
	
	private boolean existsById(long id) {
		return roleRepo.existsById(id);
	}

}
