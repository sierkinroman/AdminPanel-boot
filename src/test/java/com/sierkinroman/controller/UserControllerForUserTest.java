package com.sierkinroman.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import com.sierkinroman.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.sierkinroman.entities.dto.UserEditDto;
import com.sierkinroman.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class UserControllerForUserTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Test
    @WithUserDetails(value = "user10")
    public void testShowEditForm() throws Exception {
        this.mockMvc.perform(get("/user/edit"))
                .andExpect(status().isOk())
                .andExpect(authenticated().withRoles("USER"))
                .andExpect(xpath("//input[@id='username' and @value='user10']").exists())
                .andExpect(xpath("//div[@id='enabled_wrapper']").doesNotExist())
                .andExpect(xpath("//div[@id='roles_wrapper']").doesNotExist());
    }

    @Test
    @WithUserDetails(value = "user49")
    public void testCorrectDelete() throws Exception {
        this.mockMvc.perform(post("/user/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login"));

        assertThat(userService.findByUsername("user49")).isNull();
    }

    @Test
    @WithUserDetails(value = "user10")
    public void testCorrectUpdate() throws Exception {
        UserEditDto userEditDto = new UserEditDto(userService.findByUsername("user10"));
        userEditDto.setEmail("user10new@gmail.com");
        userEditDto.setFirstName("newFirstName");
        userEditDto.setLastName("newLastName");

        this.mockMvc.perform(post("/user/edit").flashAttr("userEditDto", userEditDto).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withRoles("USER"))
                .andExpect(redirectedUrl("/"));

        User updatedUser = userService.findByUsername("user10");
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getEmail()).isEqualTo("user10new@gmail.com");
        assertThat(updatedUser.getFirstName()).isEqualTo("newFirstName");
        assertThat(updatedUser.getLastName()).isEqualTo("newLastName");
    }

    @Test
    @WithUserDetails(value = "user10")
    public void testIncorrectUpdate() throws Exception {
        UserEditDto userEditDto = new UserEditDto(userService.findByUsername("user10"));
        userEditDto.setEmail("user02@gmail.com");

        this.mockMvc.perform(post("/user/edit").flashAttr("userEditDto", userEditDto).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(authenticated().withRoles("USER"))
                .andExpect(view().name("userEdit"))
                .andExpect(xpath("/html/body/div/form/p[4]").string("*E-mail is used"));
    }

}
