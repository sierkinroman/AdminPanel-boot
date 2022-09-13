package com.sierkinroman.service.impl;

import com.sierkinroman.entities.Role;
import com.sierkinroman.entities.repository.RoleRepository;
import com.sierkinroman.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepo;

    public RoleServiceImpl(RoleRepository roleRepo) {
        this.roleRepo = roleRepo;
    }

    @Override
    public Role findById(long id) {
        log.debug("Find Role by id '{}'", id);
        return roleRepo.findById(id).orElse(null);
    }

    @Override
    public Role findByName(String name) {
        log.debug("Find Role by name '{}'", name);
        return roleRepo.findByName(name);
    }

    @Override
    public Set<Role> findAll() {
        log.debug("Find all Roles");
        return new HashSet<>(roleRepo.findAll());
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
