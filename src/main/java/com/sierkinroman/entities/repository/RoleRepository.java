package com.sierkinroman.entities.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sierkinroman.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

}
