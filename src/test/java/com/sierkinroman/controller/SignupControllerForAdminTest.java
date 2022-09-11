package com.sierkinroman.controller;

import com.sierkinroman.entities.Role;
import com.sierkinroman.entities.User;
import com.sierkinroman.entities.dto.UserSignupDto;
import com.sierkinroman.service.RoleService;
import com.sierkinroman.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@WithUserDetails(value = "admin")
class SignupControllerForAdminTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Test
    public void testShowSignup() throws Exception {
        this.mockMvc.perform(get("/admin/addUser"))
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(xpath("/html/body/div/form/p[1]").string("Add User"))
                .andExpect(xpath("//div[@id='enabled_wrapper']").exists())
                .andExpect(xpath("//div[@id='roles_wrapper']").exists());
    }

    @Test
    public void testCorrectAddUser() throws Exception {
        UserSignupDto userSignupDto = new UserSignupDto("admin100",
                "123456",
                "123456",
                "admin100@gmail.com",
                "Admin100",
                "AdminLN100",
                true,
                Collections.singleton(roleService.findByName("ROLE_ADMIN")));

        this.mockMvc.perform(get("/admin/addUser").header("Referer", getRefererUrl()));

        this.mockMvc.perform(post("/admin/addUser").flashAttr("userSignupDto", userSignupDto).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated())
                .andExpect(redirectedUrl(getRefererUrl()));

        User addedUser = userService.findByUsername("admin100");
        assertThat(addedUser).isNotNull();
        assertThat(addedUser.isEnabled()).isTrue();
        assertThat(addedUser.getRoles()).contains(new Role("ROLE_ADMIN"));

        userService.deleteById(addedUser.getId());
    }

    private String getRefererUrl() {
        return "http://localhost:8080/admin/users/1?sortField=username&sortAsc=true";
    }

    @Test
    public void testInvalidAddUser() throws Exception {
        User persistedUser = userService.findByUsername("user1");
        UserSignupDto userSignupDto = new UserSignupDto(persistedUser.getUsername(),
                "123456",
                "1234567",
                persistedUser.getEmail(),
                persistedUser.getFirstName(),
                persistedUser.getLastName(),
                persistedUser.isEnabled(),
                Collections.emptySet());

        this.mockMvc.perform(post("/admin/addUser").flashAttr("userSignupDto", userSignupDto).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(authenticated())
                .andExpect(view().name("signup"))
                .andExpect(xpath("/html/body/div/form/p[3]").string("*Username is used"))
                .andExpect(xpath("/html/body/div/form/p[6]").string("*Confirm password is wrong"))
                .andExpect(xpath("/html/body/div/form/p[8]").string("*E-mail is used"))
                .andExpect(xpath("//div[@id='roles_wrapper']").exists())
                .andExpect(xpath("//div[@id='roles_wrapper']/p[2]").string("*Role can't be empty"));
    }

}
