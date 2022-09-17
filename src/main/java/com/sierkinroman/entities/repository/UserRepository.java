package com.sierkinroman.entities.repository;

import com.sierkinroman.entities.Role;
import com.sierkinroman.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByEmail(String email);

	Page<User> findAllByRolesIn(Set<Role> roles, Pageable pageable);

    @Query("SELECT u FROM User u WHERE lower(u.username) LIKE %:keyword%"
            + " OR lower(u.email) LIKE %:keyword%"
            + " OR lower(u.firstName) LIKE %:keyword%"
            + " OR lower(u.lastName) LIKE %:keyword%")
    Page<User> searchAll(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT u FROM User u INNER JOIN u.roles r WHERE r IN :roles" +
            " AND (lower(u.username) LIKE %:keyword%"
            + " OR lower(u.email) LIKE %:keyword%"
            + " OR lower(u.firstName) LIKE %:keyword%"
            + " OR lower(u.lastName) LIKE %:keyword%)")
    Page<User> searchAllWithRoles(@Param("keyword") String keyword, @Param("roles") Set<Role> roles, Pageable pageable);

}
