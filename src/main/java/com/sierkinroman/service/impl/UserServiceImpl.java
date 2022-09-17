package com.sierkinroman.service.impl;

import com.sierkinroman.entities.Role;
import com.sierkinroman.entities.User;
import com.sierkinroman.entities.repository.UserRepository;
import com.sierkinroman.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    public UserServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

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
        return new HashSet<>(userRepo.findAll());
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        log.debug("Find all Users with pageable '{}'", pageable);
        return userRepo.findAll(pageable);
    }

    @Override
    public Page<User> findAllWithRoles(Set<Role> roles, Pageable pageable) {
        log.debug("Find all Users with roles '{}', pageable '{}'", roles, pageable);
        return userRepo.findAllByRolesIn(roles, pageable);
    }

    @Override
    public Page<User> searchAll(String keyword, Pageable pageable) {
        log.debug("Search all Users with keyword '{}', pageable '{}'", keyword, pageable);
        return userRepo.searchAll(keyword, pageable);
    }

    @Override
    public Page<User> searchAllWithRoles(String keyword, Set<Role> roles, Pageable pageable) {
        log.debug("Search all Users with keyword '{}', roles '{}', pageable '{}'", keyword, roles, pageable);
        return userRepo.searchAllWithRoles(keyword, roles, pageable);
    }

    @Override
    public User save(User user) {
        log.debug("Save User with username '{}'", user.getUsername());
        return userRepo.save(user);
    }

    @Override
    public void update(User user) {
        log.debug("Update User with username '{}'", user.getUsername());
        userRepo.save(user);
    }

    @Override
    public void deleteById(long id) {
        log.debug("Delete User with id - '{}'", id);
        userRepo.deleteById(id);
    }

}
