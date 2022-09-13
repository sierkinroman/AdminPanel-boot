package com.sierkinroman.service;

import java.util.Set;

import com.sierkinroman.entities.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sierkinroman.entities.User;

public interface UserService {

    User findById(long id);

    User findByUsername(String username);

    User findByEmail(String email);

    Set<User> findAll();

    Page<User> findAll(Pageable pageable);

    Page<User> findAllWithRoles(Set<Role> roles, Pageable pageable);

    User save(User user);

    void update(User user);

    void deleteById(long id);

}
