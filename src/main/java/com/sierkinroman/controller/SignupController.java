package com.sierkinroman.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.sierkinroman.entities.User;
import com.sierkinroman.entities.dto.UserSignupDto;
import com.sierkinroman.service.RoleService;
import com.sierkinroman.service.UserService;
import com.sierkinroman.service.impl.userdetails.UserDetailsImpl;

@Controller
public class SignupController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping(value = {"/signup", "/admin/addUser"})
	public String showSignup(Model model) {
		model.addAttribute("userSignupDto", new UserSignupDto());
		model.addAttribute("listRoles", roleService.findAll());
		return "signup";
	}
	
	@PostMapping(value = {"/signup", "/admin/addUser"})
	public String signupUser(@ModelAttribute("userSignupDto") @Valid UserSignupDto userSignupDto,
							 BindingResult bindingResult, Model model) {
		User userByUsername = userService.findByUsername(userSignupDto.getUsername());
		User userByEmail = userService.findByEmail(userSignupDto.getEmail());
		if (userByUsername != null) {
			bindingResult.rejectValue("username", "usernameExists");
		}
		if (userByEmail != null) {
			bindingResult.rejectValue("email", "emailExists");
		}
		if (!userSignupDto.getPassword().equals(userSignupDto.getConfirmPassword())) {
			bindingResult.rejectValue("confirmPassword", "confirmPasswordWrong");
		}
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("listRoles", roleService.findAll());
			return "signup";
		} else {
			User savedUser = userSignupDto.getUser(bCryptPasswordEncoder, roleService);
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (principal instanceof UserDetailsImpl &&
					((UserDetailsImpl) principal).getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
				savedUser.setRoles(userSignupDto.getRoles());
				userService.save(savedUser);
				// TODO return to refferer page (pagination and sorting)
				return "redirect:/";
			}
			userService.save(savedUser);
		}
		return "redirect:/login";
	}
	
}
