package com.sierkinroman.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

@Controller
public class SignupController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/signup")
	public String showSignup(Model model) {
		model.addAttribute("userSignupDto", new UserSignupDto());
		return "signup";
	}
	
	@PostMapping("/signup")
	public String signupUser(@ModelAttribute("userSignupDto") @Valid UserSignupDto userSignupDto,
							 BindingResult bindingResult) {
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
			return "signup";
		} else {
			userService.save(userSignupDto.getUser(bCryptPasswordEncoder, roleService));
		}
		return "redirect:/login";
	}
	
}
