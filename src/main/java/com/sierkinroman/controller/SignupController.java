package com.sierkinroman.controller;

import com.sierkinroman.entities.User;
import com.sierkinroman.entities.dto.UserSignupDto;
import com.sierkinroman.service.RoleService;
import com.sierkinroman.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@Controller
public class SignupController {

    private final UserService userService;

    private final RoleService roleService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private String previousPage = "/";

    public SignupController(UserService userService, RoleService roleService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping(value = {"/signup", "/admin/addUser"})
    public String showSignup(Model model, HttpServletRequest request) {
        model.addAttribute("userSignupDto", new UserSignupDto());
        model.addAttribute("listRoles", roleService.findAll());
        setPreviousPage(request);
        log.info("Showing signup page");
        return "signup";
    }

    private void setPreviousPage(HttpServletRequest request) {
        previousPage = request.getHeader("Referer");
        previousPage = previousPage != null ? previousPage : "/";
        log.info("Set previousPage - '{}'", previousPage);
    }

    @PostMapping(value = "/admin/addUser")
    public String addUser(@ModelAttribute("userSignupDto") @Valid UserSignupDto userSignupDto,
                          BindingResult bindingResult,
                          Model model) {
        // check validation errors
        rejectFieldsIfNotValidForAdmin(userSignupDto, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("listRoles", roleService.findAll());
            log.info("Presents validation error - '{}'", bindingResult.getAllErrors());
            return "signup";
        }

        // save new user
        User savedUser = userSignupDto.getUser(bCryptPasswordEncoder, roleService);
        savedUser.setRoles(userSignupDto.getRoles());
        savedUser.setEnabled(userSignupDto.isEnabled());
        userService.save(savedUser);

        log.info("Admin registered a new user with username '{}'", savedUser.getUsername());
        return "redirect:" + previousPage;
    }

    private void rejectFieldsIfNotValidForAdmin(UserSignupDto userSignupDto, BindingResult bindingResult) {
        rejectFieldsIfNotValid(userSignupDto, bindingResult);
        if (userSignupDto.getRoles().isEmpty()) {
            log.info("Reject empty roles");
            bindingResult.rejectValue("roles", "roleEmpty");
        }
    }

    private void rejectFieldsIfNotValid(UserSignupDto userSignupDto, BindingResult bindingResult) {
        User userByUsername = userService.findByUsername(userSignupDto.getUsername());
        if (userByUsername != null) {
            log.info("Reject existing username '{}'", userByUsername.getUsername());
            bindingResult.rejectValue("username", "usernameExists");
        }

        User userByEmail = userService.findByEmail(userSignupDto.getEmail());
        if (userByEmail != null) {
            log.info("Reject existing email '{}'", userByEmail.getEmail());
            bindingResult.rejectValue("email", "emailExists");
        }

        if (!userSignupDto.getPassword().equals(userSignupDto.getConfirmPassword())) {
            log.info("Reject not matching confirm password");
            bindingResult.rejectValue("confirmPassword", "confirmPasswordWrong");
        }
    }

    @PostMapping(value = "/signup")
    public String signup(@ModelAttribute("userSignupDto") @Valid UserSignupDto userSignupDto,
                         BindingResult bindingResult,
                         Model model) {
        // check validation errors
        rejectFieldsIfNotValid(userSignupDto, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("listRoles", roleService.findAll());
            log.info("Presents validation error - '{}'", bindingResult.getAllErrors());
            return "signup";
        }

        // save new user
        User savedUser = userSignupDto.getUser(bCryptPasswordEncoder, roleService);
        userService.save(savedUser);
        log.info("Signup new user with username '{}'", savedUser.getUsername());

        return "redirect:/login";
    }

}
