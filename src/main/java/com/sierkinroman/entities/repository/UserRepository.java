package com.sierkinroman.entities.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sierkinroman.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByEmail(String email);

//	Page<User> findAllDistinctByRolesIn(Set<Role> roles, Pageable pageable);

    Page<User> findAllDistinctBy(Pageable pageable);

}
