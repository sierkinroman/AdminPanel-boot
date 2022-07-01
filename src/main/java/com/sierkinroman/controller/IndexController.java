package com.sierkinroman.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.sierkinroman.entities.User;
import com.sierkinroman.service.UserService;
import com.sierkinroman.service.impl.userdetails.UserDetailsImpl;

@Controller
public class IndexController {
	
	@Autowired
	UserService userService;
	
	@GetMapping("/")
	public String showUserPage(@AuthenticationPrincipal UserDetailsImpl authUser, Model model, Authentication authentication) {
		User loginedUser = userService.findByUsername(authUser.getUsername());
		model.addAttribute("loginedUser", loginedUser);
		return "userPage";
	}
	
	@GetMapping("/login")
	public String showLoginPage() {
		return "login";
	}
	
	@GetMapping("/admin")
	public String showAdminPage(@AuthenticationPrincipal UserDetailsImpl authUser, Model model) {
		User loginedUser = userService.findByUsername(authUser.getUsername());
		model.addAttribute("loginedUser", loginedUser);
		model.addAttribute("users", userService.findAll());
		return "adminPage";
	}
	
}
