package com.sierkinroman.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.sierkinroman.entities.User;
import com.sierkinroman.service.UserService;
import com.sierkinroman.service.impl.userdetails.UserDetailsImpl;

@Controller
public class IndexController {
	
	@Autowired
	private UserService userService;
	
	private int pageSize = 10;
	
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
	
	@GetMapping("/admin/users/{pageNum}")
	public String showAdminPage(@AuthenticationPrincipal UserDetailsImpl authUser, @PathVariable int pageNum, Model model) {
		User loginedUser = userService.findByUsername(authUser.getUsername());
		model.addAttribute("loginedUser", loginedUser);
		Page<User> page = userService.findPaginated(pageNum, pageSize);
		model.addAttribute("users", page.getContent());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		return "adminPage";
	}
	
}
