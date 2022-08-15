package com.sierkinroman.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

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
class UserControllerTestForUser {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Test
    @WithUserDetails(value = "user1")
    public void testShowEditForm() throws Exception {
        this.mockMvc.perform(get("/user/edit"))
                .andExpect(status().isOk())
                .andExpect(authenticated().withRoles("USER"))
                .andExpect(xpath("//input[@id='username' and @value='user1']").exists())
                .andExpect(xpath("//div[@id='roles_wrapper']").doesNotExist());
    }

    @Test
    @WithUserDetails(value = "user49")
    public void testCorrectDelete() throws Exception {
        this.mockMvc.perform(post("/user/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithUserDetails(value = "user1")
    public void testCorrectUpdate() throws Exception {
        UserEditDto userEditDto = new UserEditDto(userService.findByUsername("user1"));
        userEditDto.setEmail("user1new@gmail.com");
        userEditDto.setFirstName("newFirstName");
        userEditDto.setLastName("newLastName");

        this.mockMvc.perform(post("/user/edit").flashAttr("userEditDto", userEditDto).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withRoles("USER"))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @WithUserDetails(value = "user1")
    public void testIncorrectUpdate() throws Exception {
        UserEditDto userEditDto = new UserEditDto(userService.findByUsername("user1"));
        userEditDto.setEmail("user2@gmail.com");

        this.mockMvc.perform(post("/user/edit").flashAttr("userEditDto", userEditDto).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(authenticated().withRoles("USER"))
                .andExpect(view().name("userEdit"))
                .andExpect(xpath("/html/body/div/form/p[4]").string("*E-mail is used"));
    }

}
