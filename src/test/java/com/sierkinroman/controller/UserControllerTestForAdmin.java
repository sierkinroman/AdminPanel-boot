package com.sierkinroman.controller;

import com.sierkinroman.entities.Role;
import com.sierkinroman.entities.User;
import com.sierkinroman.entities.dto.UserEditDto;
import com.sierkinroman.service.RoleService;
import com.sierkinroman.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class UserControllerTestForAdmin {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    public void setup() {
        User admin2 = User.builder()
                .username("admin2")
                .password(bCryptPasswordEncoder.encode("123456"))
                .email("admin2@gmail.com")
                .firstName("Admin2")
                .lastName("AdminLN2")
                .enabled(true)
                .roles(Collections.singleton(roleService.findByName("ROLE_ADMIN")))
                .build();
        userService.save(admin2);
    }

    @AfterEach
    public void tearDown() {
        User admin2 = userService.findByUsername("admin2");
        if (admin2 != null) {
            userService.deleteById(admin2.getId());
        }
    }

    @Test
    @WithUserDetails(value = "admin")
    public void testShowEditForm() throws Exception {
        long id = userService.findByUsername("admin").getId();

        this.mockMvc.perform(get("/user/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withRoles("ADMIN"))
                .andExpect(redirectedUrl("/admin/" + id + "/edit"));

        this.mockMvc.perform(get("/admin/{id}/edit", 1))
                .andExpect(status().isOk())
                .andExpect(authenticated().withRoles("ADMIN"))
                .andExpect(xpath("//input[@id='username' and @value='admin']").exists())
                .andExpect(xpath("//div[@id='roles_wrapper']").exists());
    }

    @Test
    @WithUserDetails(value = "admin")
    public void testIncorrectDeleteSelf_LastAdmin() throws Exception {
        tearDown();

        this.mockMvc.perform(post("/admin/{id}/delete", 1).header("Referer", getRefererUrl()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withRoles("ADMIN"))
                .andExpect(redirectedUrl(getRefererUrl()));
    }

    private String getRefererUrl() {
        return "http://localhost:8080/admin/users/1?sortField=username&sortAsc=true";
    }

    @Test
    @WithUserDetails(value = "admin2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testCorrectDeleteSelf_NotLastAdmin() throws Exception {
        long userId = userService.findByUsername("admin2").getId();

        this.mockMvc.perform(post("/admin/{id}/delete", userId).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithUserDetails(value = "admin")
    public void testCorrectDelete_NotLastAdmin() throws Exception {
        long userId = userService.findByUsername("admin2").getId();

        this.mockMvc.perform(post("/admin/{id}/delete", userId).header("Referer", getRefererUrl()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withRoles("ADMIN"))
                .andExpect(redirectedUrl(getRefererUrl()));
    }

    @Test
    @WithUserDetails(value = "admin2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testCorrectUpdateSelf() throws Exception {
        User admin2 = userService.findByUsername("admin2");

        UserEditDto userEditDto = new UserEditDto(admin2);
        userEditDto.setEmail("admin2.new@gmail.com");
        userEditDto.setFirstName("Admin2NewName");
        userEditDto.setLastName("Admin2NewLastName");

        this.mockMvc.perform(get("/admin/{id}/edit", admin2.getId()).header("Referer", getRefererUrl()));

        this.mockMvc.perform(post("/admin/{id}/edit", admin2.getId()).flashAttr("userEditDto", userEditDto).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withRoles("ADMIN"))
                .andExpect(redirectedUrl(getRefererUrl()));
    }

    @Test
    @WithUserDetails(value = "admin")
    public void testIncorrectUpdateSelf_RemoveRoleAdmin_FromLastAdmin() throws Exception {
        tearDown();
        User admin = userService.findByUsername("admin");

        this.mockMvc.perform(get("/admin/{id}/edit", admin.getId()).header("Referer", getRefererUrl()));

        this.mockMvc.perform(post("/admin/{id}/edit", admin.getId()).flashAttr("userEditDto", getNotAdminEditDto(admin)).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withRoles("ADMIN"))
                .andExpect(redirectedUrl(getRefererUrl()));

    }

    private UserEditDto getNotAdminEditDto(User user) {
        UserEditDto userEditDto = new UserEditDto(user);
        HashSet<Role> roles = new HashSet<>();
        roles.add(roleService.findByName("ROLE_USER"));
        userEditDto.setRoles(roles);
        return userEditDto;
    }

    @Test
    @WithUserDetails(value = "admin2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testCorrectUpdateSelf_RemoveRoleAdmin_FromNotLastAdmin() throws Exception {
        User admin2 = userService.findByUsername("admin2");

        this.mockMvc.perform(get("/admin/{id}/edit", admin2.getId()).header("Referer", getRefererUrl()));

        this.mockMvc.perform(post("/admin/{id}/edit", admin2.getId()).flashAttr("userEditDto", getNotAdminEditDto(admin2)).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withRoles("USER"))
                .andExpect(redirectedUrl("/"));

        this.mockMvc.perform(get("/user/edit"))
                .andExpect(status().isOk())
                .andExpect(authenticated().withRoles("USER"))
                .andExpect(xpath("//div[@id='roles_wrapper']").doesNotExist());
    }

    @Test
    @WithUserDetails(value = "admin")
    public void testIncorrectUpdate_EmptyRoles() throws Exception {
        User admin = userService.findByUsername("admin");
        UserEditDto emptyRolesEditDto = new UserEditDto(admin);
        emptyRolesEditDto.setRoles(Collections.emptySet());

        this.mockMvc.perform(post("/admin/{id}/edit", admin.getId()).flashAttr("userEditDto", emptyRolesEditDto).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(authenticated().withRoles("ADMIN"))
                .andExpect(xpath("//div[@id='roles_wrapper']/p[2]").string("Role can't be empty"));
    }

    @Test
    @WithUserDetails(value = "admin")
    public void testCorrectUpdate_DisableUser() throws Exception {
        User admin2 = userService.findByUsername("admin2");
        UserEditDto disabledEditDto = new UserEditDto(admin2);
        disabledEditDto.setEnabled(false);

        this.mockMvc.perform(get("/admin/{id}/edit", admin2.getId()).header("Referer", getRefererUrl()));

        this.mockMvc.perform(post("/admin/{id}/edit", admin2.getId()).flashAttr("userEditDto", disabledEditDto).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withRoles("ADMIN"))
                .andExpect(redirectedUrl(getRefererUrl()));

        assertThat(userService.findByUsername("admin2").isEnabled()).isFalse();


//        this.mockMvc.perform(formLogin().user(admin2.getUsername()).password(admin2.getPassword()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(unauthenticated())
//                .andExpect(redirectedUrl("/login?error=true"));

//        this.mockMvc.perform(post("/logout").with(csrf()))
//                .andExpect(unauthenticated());
//
//        this.mockMvc.perform(get("/login"))
//                .andExpect(status().isOk())
//                .andExpect(unauthenticated());
//
//        this.mockMvc.perform(post("/login").param("username", admin2.getUsername()).param("password", admin2.getPassword()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(unauthenticated())
//                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    @WithUserDetails(value = "admin")
    public void testCorrectUpdate_EnableUser() throws Exception {
        User user2 = userService.findByUsername("user2");
        assertThat(user2.isEnabled()).isFalse();

        UserEditDto enabledEditDto = new UserEditDto(user2);
        enabledEditDto.setEnabled(true);

        this.mockMvc.perform(get("/admin/{id}/edit", user2.getId()).header("Referer", getRefererUrl()));

        this.mockMvc.perform(post("/admin/{id}/edit", user2.getId()).flashAttr("userEditDto", enabledEditDto).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withRoles("ADMIN"))
                .andExpect(redirectedUrl(getRefererUrl()));

        assertThat(userService.findByUsername("user2").isEnabled()).isTrue();
    }

}
