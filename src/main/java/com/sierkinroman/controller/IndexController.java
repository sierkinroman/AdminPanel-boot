package com.sierkinroman.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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
	public String showAdminPage(@AuthenticationPrincipal UserDetailsImpl authUser,
			@PathVariable int pageNum,
			@RequestParam(defaultValue = "username") String sortField,
			@RequestParam(defaultValue = "true") String sortAsc,
			Model model) {
		User loginedUser = userService.findByUsername(authUser.getUsername());
		model.addAttribute("loginedUser", loginedUser);

		Sort sorting = sortAsc.equals("true") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();	
		Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sorting); 
		Page<User> page = userService.findAll(pageable);

		model.addAttribute("users", page.getContent());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortAsc", sortAsc);
		
		return "adminPage";
	}
	
}
