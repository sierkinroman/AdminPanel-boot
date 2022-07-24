package com.sierkinroman.controller;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sierkinroman.entities.User;
import com.sierkinroman.entities.Role;
import com.sierkinroman.entities.dto.UserEditDto;
import com.sierkinroman.service.RoleService;
import com.sierkinroman.service.UserService;
import com.sierkinroman.service.impl.userdetails.UserDetailsImpl;

@Controller
public class UserController {
	
	// TODO change color and transparent in toastr
	// TODO Refactor 
	// TODO Add logging
	
	private String previousPage = "/";
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	@GetMapping(value = {"/admin/{id}/edit", "/user/edit"})
	public String showEditForm(@PathVariable Optional<Long> id, Model model, 
							   HttpServletRequest request) {
		model.addAttribute("userEditDto", new UserEditDto(userService
			.findById(id.orElseGet(() -> {
				UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				return user.getId();
			})
		)));
		model.addAttribute("listRoles", roleService.findAll());
		previousPage = request.getHeader("Referer");
		previousPage = previousPage != null ? previousPage : "/";
		return "userEdit";
	}
	
	@PostMapping(value = {"/admin/{id}/edit", "/user/edit"})
	public String updateUser(@PathVariable Optional<Long> id,
			@AuthenticationPrincipal UserDetailsImpl authUser,
			@ModelAttribute("userEditDto") @Valid UserEditDto userEditDto,
			BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
		long userId = id.orElseGet(() -> {
			return authUser.getId();
		});
		
		if (userId == authUser.getId()) {
			if (authUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
					&& !userEditDto.getRoles().contains(new Role("ROLE_ADMIN"))
					&& roleService.findByName("ROLE_ADMIN").getUsers().size() == 1) {
				redirectAttributes.addFlashAttribute("action", "lastAdminInvalidEdit");
				return "redirect:" + previousPage;
			}
		}
		
		User userByEmail = userService.findByEmail(userEditDto.getEmail());
		if (userByEmail != null) {
			if (userByEmail.getId() != userId) {
				bindingResult.rejectValue("email", "emailExists");
			}
		}
		if (bindingResult.hasErrors()) {
			model.addAttribute("listRoles", roleService.findAll());
			return "userEdit";
		}
		
		User updatedUser = userService.findById(userId);
		updatedUser.setEmail(userEditDto.getEmail());
		updatedUser.setFirstName(userEditDto.getFirstName());
		updatedUser.setLastName(userEditDto.getLastName());
		if (authUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
			updatedUser.setRoles(userEditDto.getRoles());
		}
		userService.update(updatedUser);

		redirectAttributes.addFlashAttribute("action", "successEdit");

		if (userId == authUser.getId()
				&& authUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
				&& !userEditDto.getRoles().contains(new Role("ROLE_ADMIN"))) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			Collection<GrantedAuthority> updatedAuthorities = new HashSet<>();
			for (Role role : updatedUser.getRoles()) {
				updatedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
			}
			Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), updatedAuthorities);
			SecurityContextHolder.getContext().setAuthentication(newAuth);
			return "redirect:/";
		}
		
		
		return "redirect:" + previousPage;
	}
	
	@PostMapping(value = {"/admin/{id}/delete", "/user/delete"})
	public String deleteUser(@PathVariable Optional<Long> id, @AuthenticationPrincipal UserDetailsImpl authUser, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		long userId = id.orElseGet(() -> {
			return authUser.getId();
		});
		
		previousPage = request.getHeader("Referer");
		previousPage = previousPage != null ? previousPage : "/";
		
		if (userId == authUser.getId()) {
			if (authUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) 
					&& roleService.findByName("ROLE_ADMIN").getUsers().size() == 1) {
				redirectAttributes.addFlashAttribute("action", "lastAdminInvalidDelete");
				return "redirect:" + previousPage;
			}
		}
		
		userService.deleteById(userId);
		if (userId == authUser.getId()) {
			try {
				request.logout();
			} catch (ServletException e) {
				e.printStackTrace();
			}
			return "redirect:/login";
		}
		
		redirectAttributes.addFlashAttribute("action", "successDelete");
		return "redirect:" + previousPage;
	}
	
}
