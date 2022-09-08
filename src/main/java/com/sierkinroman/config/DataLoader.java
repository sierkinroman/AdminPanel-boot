package com.sierkinroman.config;

import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.sierkinroman.entities.Role;
import com.sierkinroman.entities.User;
import com.sierkinroman.service.RoleService;
import com.sierkinroman.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DataLoader {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        log.info("Loading initial data in database");
        createRoleIfNotExists("ROLE_ADMIN");
        createRoleIfNotExists("ROLE_USER");

        createUserIfNotExists("admin",
                "admin",
                "admin@gmail.com",
                "Admin",
                "Admin",
                true,
                Collections.singleton(roleService.findByName("ROLE_ADMIN")));

        Set<Role> roleUser = Collections.singleton(roleService.findByName("ROLE_USER"));
        int countUser = 49;
        for (int i = 1; i <= countUser; i++) {
            if (i < 10) {
                createUserIfNotExists("user" + i,
                        "user" + i,
                        "user" + i + "@gmail.com",
                        "User" + i,
                        "UserLN" + i,
                        false,
                        roleUser);
            } else {
                createUserIfNotExists("user" + i,
                        "user" + i,
                        "user" + i + "@gmail.com",
                        "User" + i,
                        "UserLN" + i,
                        true,
                        roleUser);
            }
        }
        log.info("Initial data has loaded in database");
    }

    private void createRoleIfNotExists(String name) {
        if (roleService.findByName(name) == null) {
            Role role = new Role(name);
            roleService.save(role);
            log.info("Created Role with name - '{}'", name);
        }
    }

    private void createUserIfNotExists(String username,
                                       String password,
                                       String email,
                                       String firstName,
                                       String lastName,
                                       boolean enabled,
                                       Set<Role> roles) {
        if (userService.findByUsername(username) == null
                && userService.findByEmail(email) == null) {
            User user = User.builder()
                    .username(username)
                    .password(bCryptPasswordEncoder.encode(password))
                    .email(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .enabled(enabled)
                    .roles(roles)
                    .build();
            userService.save(user);
            log.info("Created User with username - '{}'", username);
        }
    }

}
