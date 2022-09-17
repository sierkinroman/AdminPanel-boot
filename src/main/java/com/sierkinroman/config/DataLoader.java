package com.sierkinroman.config;

import com.sierkinroman.entities.Role;
import com.sierkinroman.entities.User;
import com.sierkinroman.service.RoleService;
import com.sierkinroman.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Component
public class DataLoader {

    private final UserService userService;

    private final RoleService roleService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public DataLoader(UserService userService, RoleService roleService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

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
            String number = (i < 10 ? "0" : "") + i;
            createUserIfNotExists("user" + number,
                    "user" + number,
                    "user" + number + "@gmail.com",
                    "User" + number,
                    "UserLN" + number,
                    i >= 10,
                    roleUser);
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
