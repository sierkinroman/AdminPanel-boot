package com.sierkinroman.controller;

import com.sierkinroman.entities.Role;
import com.sierkinroman.entities.User;
import com.sierkinroman.service.RoleService;
import com.sierkinroman.service.UserService;
import com.sierkinroman.service.impl.userdetails.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Controller
public class IndexController {

    private final UserService userService;

    private final RoleService roleService;

    private int pageSize = 10;

    public IndexController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        log.info("Show login page");
        return "login";
    }

    @GetMapping("/")
    public String showUserPage(@AuthenticationPrincipal UserDetailsImpl authUser, Model model) {
        model.addAttribute("loginedUser", userService.findByUsername(authUser.getUsername()));
        log.info("Show homepage for user with id '{}'", authUser.getId());
        return "userPage";
    }

    @GetMapping("/admin/users/{pageNumber}")
    public String showAdminPage(@AuthenticationPrincipal UserDetailsImpl authUser,
                                @PathVariable int pageNumber,
                                @RequestParam(defaultValue = "username") String sortField,
                                @RequestParam(defaultValue = "true") String sortAsc,
                                @RequestParam(defaultValue = "All") String checkedRole,
                                @RequestParam(required = false) String keyword,
                                Model model) {
        model.addAttribute("loginedUser", userService.findByUsername(authUser.getUsername()));

        Sort sort = sortAsc.equals("true")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        Page<User> page;
        if (keyword == null) {
            if (checkedRole.equals("All")) {
                page = userService.findAll(pageable);
            } else {
                page = userService.findAllWithRoles(getCheckedRoles(checkedRole), pageable);
            }
        } else {
            model.addAttribute("keyword", keyword);
            if (checkedRole.equals("All")) {
                page = userService.searchAll(keyword.toLowerCase(), pageable);
            } else {
                page = userService.searchAllWithRoles(keyword.toLowerCase(), getCheckedRoles(checkedRole), pageable);
            }
        }

        model.addAttribute("users", page.getContent());
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", page.getTotalPages());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortAsc", sortAsc);

        model.addAttribute("roles", getAllRoleNames());
        model.addAttribute("checkedRole", checkedRole);

        log.info("Show adminPage for user with id '{}', pageNumber = '{}', sortField = '{}', sortAsc = '{}', checkedRole = '{}', keyword = '{}'",
                authUser.getId(), pageNumber, sortField, sortAsc, checkedRole, keyword);

        return "adminPage";
    }

    private Set<Role> getCheckedRoles(String checkedRole) {
        Set<Role> checkedRoles = new HashSet<>();
        checkedRoles.add(roleService.findByName(checkedRole));
        return checkedRoles;
    }

    private List<String> getAllRoleNames() {
        List<String> roles = new ArrayList<>();
        roles.add("All");
        roleService.findAll().forEach(role -> roles.add(role.getName()));
        return roles;
    }

}
