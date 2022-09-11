package com.sierkinroman.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.sierkinroman.entities.Role;
import com.sierkinroman.entities.User;
import com.sierkinroman.entities.dto.UserSignupDto;
import com.sierkinroman.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
class SignupControllerForAnonymousTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Test
    public void testShowSignup() throws Exception {
        this.mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(unauthenticated())
                .andExpect(xpath("/html/body/div/form/p[1]").string("Sign up"))
                .andExpect(xpath("//div[@id='enabled_wrapper']").doesNotExist())
                .andExpect(xpath("//div[@id='roles_wrapper']").doesNotExist());
    }

    @Test
    public void testCorrectSignup() throws Exception {
        UserSignupDto userSignupDto = new UserSignupDto("user100",
                "123456",
                "123456",
                "user100@gmail.com",
                "User100",
                "UserLN100",
                true,
                null);
        this.mockMvc.perform(post("/signup").flashAttr("userSignupDto", userSignupDto).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login"));

        User registeredUser = userService.findByUsername("user100");

        assertThat(registeredUser).isNotNull();
        assertThat(registeredUser.getRoles()).contains(new Role("ROLE_USER"));
    }

    @Test
    public void testInvalidSignup() throws Exception {
        UserSignupDto userSignupDto = new UserSignupDto("user1",
                "123456",
                "1234567",
                "user1@gmail.com",
                "User1",
                "UserLN1",
                true,
                null);
        this.mockMvc.perform(post("/signup").flashAttr("userSignupDto", userSignupDto).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(unauthenticated())
                .andExpect(view().name("signup"))
                .andExpect(xpath("/html/body/div/form/p[3]").string("*Username is used"))
                .andExpect(xpath("/html/body/div/form/p[6]").string("*Confirm password is wrong"))
                .andExpect(xpath("/html/body/div/form/p[8]").string("*E-mail is used"));
    }

}
