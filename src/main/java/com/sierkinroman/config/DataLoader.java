package com.sierkinroman.config;

import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sierkinroman.entities.Role;
import com.sierkinroman.entities.User;
import com.sierkinroman.service.RoleService;
import com.sierkinroman.service.UserService;

@Component
public class DataLoader {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@EventListener(ApplicationReadyEvent.class)
    @Transactional
	public void runAfterStartup() {
		createUserIfNotExists("admin",
				  "admin",
				  "admin@gmail.com",
				  "Admin",
				  "Admin",
				  Collections.singleton(roleService.findByName("ROLE_ADMIN")));
		
		Set<Role> roleUser = Collections.singleton(roleService.findByName("ROLE_USER"));
		int countUser = 50;
		for (int i = 1; i <= countUser; i++) {
			createUserIfNotExists("user" + i,
								  "user" + i,
								  "user" + i + "@gmail.com",
								  "User" + i,
								  "UserLN" + i,
								  roleUser);
		}	
	}
	
    @Transactional
	private void createUserIfNotExists(String username,
									   String password,
									   String email,
									   String firstName,
									   String lastName,
									   Set<Role> roles) {
		if (userService.findByUsername(username) == null
				&& userService.findByEmail(email) == null) {
			User user = new User(username,
								 bCryptPasswordEncoder.encode(password),
								 email,
								 firstName,
								 lastName,
								 roles);
			userService.save(user);
		}
	}

}
