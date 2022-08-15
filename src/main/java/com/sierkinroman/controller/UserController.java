package com.sierkinroman.controller;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class UserController {

    // TODO change color and transparent in toastr
    // TODO while edit user, user' role can't be empty (or default ROLE_USER)

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    private String previousPage = "/";

    @GetMapping(value = "/admin/{id}/edit")
    public String showEditPageToSpecifiedUser(@PathVariable long id,
                                              Model model,
                                              HttpServletRequest request) {
        model.addAttribute("userEditDto", new UserEditDto(userService.findById(id)));
        model.addAttribute("listRoles", roleService.findAll());
        setPreviousPage(request);
        log.info("Show userEdit page for user with id '{}'", id);
        return "userEdit";
    }

    private void setPreviousPage(HttpServletRequest request) {
        previousPage = request.getHeader("Referer");
        previousPage = previousPage != null ? previousPage : "/";
        log.info("Set previousPage - '{}'", previousPage);
    }

    @GetMapping(value = "/user/edit")
    public String showEditPage(@AuthenticationPrincipal UserDetailsImpl authUser,
                               Model model) {
        if (authUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin/" + authUser.getId() + "/edit";
        }
        model.addAttribute("userEditDto", new UserEditDto(userService.findById(authUser.getId())));
        model.addAttribute("listRoles", roleService.findAll());
        log.info("Show userEdit page for user with id '{}'", authUser.getId());
        return "userEdit";
    }

    @PostMapping(value = "/admin/{id}/edit")
    public String updateSpecifiedUser(@PathVariable long id,
                                      @AuthenticationPrincipal UserDetailsImpl authUser,
                                      @ModelAttribute("userEditDto") @Valid UserEditDto userEditDto,
                                      BindingResult bindingResult,
                                      Model model,
                                      RedirectAttributes redirectAttributes,
                                      HttpServletRequest request) {
        // if Last Admin remove ROLE_ADMIN from self
        if (id == authUser.getId()
                && isLastAdminChangeSelfRoleToNonAdmin(userEditDto)) {
            redirectAttributes.addFlashAttribute("action", "lastAdminInvalidEdit");
            log.info("Last Admin with id - '{}' can't remove ROLE_ADMIN from self", id);
            return "redirect:" + previousPage;
        }

        // check validation errors
        rejectEmailIfExists(userEditDto, id, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("listRoles", roleService.findAll());
            log.info("Presents validation error - '{}'", bindingResult.getAllErrors());
            return "userEdit";
        }

        // update
        User updatedUser = userService.findById(id);
        updatedUser.setEmail(userEditDto.getEmail());
        updatedUser.setFirstName(userEditDto.getFirstName());
        updatedUser.setLastName(userEditDto.getLastName());
        updatedUser.setRoles(userEditDto.getRoles());
        userService.update(updatedUser);
        log.info("Updated user with id '{}'", id);

        redirectAttributes.addFlashAttribute("action", "successEdit");

        // if authenticated Admin remove ROLE_ADMIN than update authorities in security context
        if (id == authUser.getId()
                && !userEditDto.getRoles().contains(new Role("ROLE_ADMIN"))) {
            log.info("Authenticated Admin with id '{}' remove ROLE_ADMIN from self", id);
            setAuthoritiesInAuthentication(updatedUser.getRoles());
            return "redirect:/";
        }

        return "redirect:" + previousPage;
    }

    private boolean isLastAdminChangeSelfRoleToNonAdmin(UserEditDto userEditDto) {
        return !userEditDto.getRoles().contains(new Role("ROLE_ADMIN"))
                && roleService.findByName("ROLE_ADMIN").getUsers().size() == 1;
    }

    private void rejectEmailIfExists(UserEditDto userEditDto,
                                     long updatedUserId,
                                     BindingResult bindingResult) {
        User userByEmail = userService.findByEmail(userEditDto.getEmail());
        if (userByEmail != null
                && userByEmail.getId() != updatedUserId) {
            bindingResult.rejectValue("email", "emailExists");
            log.info("Reject email, '{}' is present in db", userEditDto.getEmail());
        }
    }

    private void setAuthoritiesInAuthentication(Set<Role> roles) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Collection<GrantedAuthority> updatedAuthorities = new HashSet<>();
        for (Role role : roles) {
            updatedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        User authUser = userService.findByUsername(auth.getName());
        authUser.setRoles(roles);
        UserDetailsImpl principal = new UserDetailsImpl(authUser);

        Authentication newAuth = new UsernamePasswordAuthenticationToken(principal, auth.getCredentials(), updatedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        log.info("Update user's authorities in security context");
    }

    @PostMapping(value = "/user/edit")
    public String updateUser(@AuthenticationPrincipal UserDetailsImpl authUser,
                             @ModelAttribute("userEditDto") @Valid UserEditDto userEditDto,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        // check validation errors
        rejectEmailIfExists(userEditDto, authUser.getId(), bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("listRoles", roleService.findAll());
            log.info("Presents validation error - '{}'", bindingResult.getAllErrors());
            return "userEdit";
        }

        // update
        User updatedUser = userService.findById(authUser.getId());
        updatedUser.setEmail(userEditDto.getEmail());
        updatedUser.setFirstName(userEditDto.getFirstName());
        updatedUser.setLastName(userEditDto.getLastName());
        userService.update(updatedUser);
        log.info("Updated user with id '{}'", authUser.getId());

        redirectAttributes.addFlashAttribute("action", "successEdit");

        return "redirect:/";
    }

    @PostMapping(value = "/admin/{id}/delete")
    public String deleteSpecifiedUser(@PathVariable long id,
                                      @AuthenticationPrincipal UserDetailsImpl authUser,
                                      HttpServletRequest request,
                                      RedirectAttributes redirectAttributes) {
        // if Last Admin delete self
        if (authUser.getId() == id) {
            if (roleService.findByName("ROLE_ADMIN").getUsers().size() == 1) {
                redirectAttributes.addFlashAttribute("action", "lastAdminInvalidDelete");
                log.info("Last Admin with id '{}' can't delete self", id);
                setPreviousPage(request);
                return "redirect:" + previousPage;
            }
        }

        userService.deleteById(id);
        log.info("Deleted user with id '{}'", id);

        if (authUser.getId() == id) {
            logout(request);
            return "redirect:/login";
        }

        redirectAttributes.addFlashAttribute("action", "successDelete");
        setPreviousPage(request);
        return "redirect:" + previousPage;
    }

    private void logout(HttpServletRequest request) {
        try {
            String username = request.getUserPrincipal().getName();
            request.logout();
            log.info("Logout authenticated user '{}'", username);
        } catch (ServletException e) {
            log.error("Exception while logout: ", e);
        }
    }

    @PostMapping(value = "/user/delete")
    public String deleteUser(@AuthenticationPrincipal UserDetailsImpl authUser,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {
        userService.deleteById(authUser.getId());
        log.info("Deleted user with id '{}'", authUser.getId());
        logout(request);
        return "redirect:/login";
    }

}
