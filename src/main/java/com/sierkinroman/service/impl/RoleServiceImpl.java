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
        return roleRepo.findById(id).orElse(null);
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
        log.debug("Save Role with name '{}'", role.getName());
        return roleRepo.save(role);
    }

    @Override
    public void update(Role role) {
        log.debug("Update Role with name '{}'", role.getName());
        roleRepo.save(role);
    }

}
