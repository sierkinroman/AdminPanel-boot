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
import static org.assertj.core.api.Assertions.fail;
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
class UserControllerForAdminTest {

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

        this.mockMvc.perform(get("/admin/{id}/edit", id))
                .andExpect(status().isOk())
                .andExpect(authenticated().withRoles("ADMIN"))
                .andExpect(xpath("//input[@id='username' and @value='admin']").exists())
                .andExpect(xpath("//div[@id='enabled_wrapper']").exists())
                .andExpect(xpath("//div[@id='roles_wrapper']").exists());
    }

    @Test
    @WithUserDetails(value = "admin")
    public void testIncorrectDeleteSelf_LastEnabledAdmin() throws Exception {
        disableUser("admin2");

        this.mockMvc.perform(post("/admin/{id}/delete", 1).header("Referer", getRefererUrl()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withRoles("ADMIN"))
                .andExpect(redirectedUrl(getRefererUrl()));

        assertThat(userService.findByUsername("admin")).isNotNull();
    }

    private void disableUser(String username) {
        User user = userService.findByUsername(username);
        if (user != null) {
            user.setEnabled(false);
            userService.save(user);
        } else {
            fail("User with username '" + username + "' is not present in database");
        }
    }

    private String getRefererUrl() {
        return "http://localhost:8080/admin/users/1?sortField=username&sortAsc=true";
    }

    @Test
    @WithUserDetails(value = "admin2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testCorrectDeleteSelf() throws Exception {
        long userId = userService.findByUsername("admin2").getId();

        this.mockMvc.perform(post("/admin/{id}/delete", userId).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login"));

        assertThat(userService.findByUsername("admin2")).isNull();
    }

    @Test
    @WithUserDetails(value = "admin")
    public void testCorrectDelete() throws Exception {
        long userId = userService.findByUsername("admin2").getId();

        this.mockMvc.perform(post("/admin/{id}/delete", userId).header("Referer", getRefererUrl()).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withRoles("ADMIN"))
                .andExpect(redirectedUrl(getRefererUrl()));

        assertThat(userService.findByUsername("admin2")).isNull();
    }

    @Test
    @WithUserDetails(value = "admin2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testCorrectUpdateSelf() throws Exception {
        User admin2 = userService.findByUsername("admin2");
        UserEditDto userEditDto = new UserEditDto(admin2);
        userEditDto.setEmail("admin2new@gmail.com");
        userEditDto.setFirstName("Admin2NewName");
        userEditDto.setLastName("Admin2NewLastName");

        this.mockMvc.perform(get("/admin/{id}/edit", admin2.getId()).header("Referer", getRefererUrl()));

        this.mockMvc.perform(post("/admin/{id}/edit", admin2.getId()).flashAttr("userEditDto", userEditDto).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withRoles("ADMIN"))
                .andExpect(redirectedUrl(getRefererUrl()));

        User updatedAdmin = userService.findByUsername("admin2");
        assertThat(updatedAdmin).isNotNull();
        assertThat(updatedAdmin.getEmail()).isEqualTo("admin2new@gmail.com");
        assertThat(updatedAdmin.getFirstName()).isEqualTo("Admin2NewName");
        assertThat(updatedAdmin.getLastName()).isEqualTo("Admin2NewLastName");
    }

    @Test
    @WithUserDetails(value = "admin")
    public void testIncorrectUpdateSelf_RemoveRoleAdmin_FromLastEnabledAdmin() throws Exception {
        disableUser("admin2");
        User admin = userService.findByUsername("admin");

        this.mockMvc.perform(get("/admin/{id}/edit", admin.getId()).header("Referer", getRefererUrl()));

        this.mockMvc.perform(post("/admin/{id}/edit", admin.getId()).flashAttr("userEditDto", getNotAdminEditDto(admin)).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withRoles("ADMIN"))
                .andExpect(redirectedUrl(getRefererUrl()));

        User notUpdatedUser = userService.findByUsername("admin");
        assertThat(notUpdatedUser).isNotNull();
        assertThat(notUpdatedUser.getRoles()).contains(new Role("ROLE_ADMIN"));
    }

    private UserEditDto getNotAdminEditDto(User user) {
        HashSet<Role> roles = new HashSet<>();
        roles.add(roleService.findByName("ROLE_USER"));

        UserEditDto userEditDto = new UserEditDto(user);
        userEditDto.setRoles(roles);
        return userEditDto;
    }

    @Test
    @WithUserDetails(value = "admin2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testCorrectUpdateSelf_RemoveRoleAdmin() throws Exception {
        User admin2 = userService.findByUsername("admin2");

        this.mockMvc.perform(get("/admin/{id}/edit", admin2.getId()).header("Referer", getRefererUrl()));

        this.mockMvc.perform(post("/admin/{id}/edit", admin2.getId()).flashAttr("userEditDto", getNotAdminEditDto(admin2)).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withRoles("USER"))
                .andExpect(redirectedUrl("/"));

        User updatedUser = userService.findByUsername("admin2");
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getRoles()).doesNotContain(new Role("ROLE_ADMIN"));

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
                .andExpect(xpath("//div[@id='roles_wrapper']/p[2]").string("*Role can't be empty"));
    }

    @Test
    @WithUserDetails(value = "admin")
    public void testCorrectDisable() throws Exception {
        User admin2 = userService.findByUsername("admin2");
        UserEditDto disabledEditDto = getUserEditDto(admin2, false);

        this.mockMvc.perform(get("/admin/{id}/edit", admin2.getId()).header("Referer", getRefererUrl()));

        this.mockMvc.perform(post("/admin/{id}/edit", admin2.getId()).flashAttr("userEditDto", disabledEditDto).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withRoles("ADMIN"))
                .andExpect(redirectedUrl(getRefererUrl()));

        assertThat(userService.findByUsername("admin2").isEnabled()).isFalse();
    }

    private UserEditDto getUserEditDto(User user, boolean enabled) {
        UserEditDto userEditDto = new UserEditDto(user);
        userEditDto.setEnabled(enabled);
        return userEditDto;
    }

    @Test
    @WithUserDetails(value = "admin")
    public void testCorrectEnable() throws Exception {
        User user2 = userService.findByUsername("user02");
        assertThat(user2.isEnabled()).isFalse();
        UserEditDto enabledEditDto = getUserEditDto(user2, true);

        this.mockMvc.perform(get("/admin/{id}/edit", user2.getId()).header("Referer", getRefererUrl()));

        this.mockMvc.perform(post("/admin/{id}/edit", user2.getId()).flashAttr("userEditDto", enabledEditDto).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withRoles("ADMIN"))
                .andExpect(redirectedUrl(getRefererUrl()));

        assertThat(userService.findByUsername("user02").isEnabled()).isTrue();
    }

    @Test
    @WithUserDetails(value = "admin")
    public void testIncorrectDisableSelf_LastEnabledAdmin() throws Exception {
        disableUser("admin2");
        User admin = userService.findByUsername("admin");
        UserEditDto disabledEditDto = getUserEditDto(admin, false);

        this.mockMvc.perform(get("/admin/{id}/edit", admin.getId()).header("Referer", getRefererUrl()));

        this.mockMvc.perform(post("/admin/{id}/edit", admin.getId()).flashAttr("userEditDto", disabledEditDto).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withRoles("ADMIN"))
                .andExpect(redirectedUrl(getRefererUrl()));

        assertThat(userService.findByUsername("admin").isEnabled()).isTrue();
    }

    @Test
    @WithUserDetails(value = "admin2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testCorrectDisableSelf() throws Exception {
        User admin2 = userService.findByUsername("admin2");
        UserEditDto disabledEditDto = getUserEditDto(admin2, false);

        this.mockMvc.perform(get("/admin/{id}/edit", admin2.getId()).header("Referer", getRefererUrl()));

        this.mockMvc.perform(post("/admin/{id}/edit", admin2.getId()).flashAttr("userEditDto", disabledEditDto).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login"));

        User updatedUser = userService.findByUsername("admin2");
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.isEnabled()).isFalse();
    }

}
