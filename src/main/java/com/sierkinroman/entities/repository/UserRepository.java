package com.sierkinroman.entities.repository;

import com.sierkinroman.entities.Role;
import com.sierkinroman.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByEmail(String email);

	Page<User> findAllByRolesIn(Set<Role> roles, Pageable pageable);

}
