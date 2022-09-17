package com.sierkinroman.controller;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@WithUserDetails(value = "admin")
public class IndexControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testShowUserPage() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername("admin").withRoles("ADMIN"))
                .andExpect(xpath("/html/body/div[1]/p/span").string("admin"));
    }

    @Test
    @WithAnonymousUser
    public void testShowLoginPage() throws Exception {
        this.mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(unauthenticated())
                .andExpect(view().name("login"));
    }

    @Test
    public void testShowAdminPage() throws Exception {
        this.mockMvc.perform(get("/admin/users/1"))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername("admin").withRoles("ADMIN"))
                .andExpect(xpath("/html/body/table").exists());
    }

    @Test
    public void testShowAdminPage_SortUsernameAsc() throws Exception {
        this.mockMvc.perform(get("/admin/users/1").queryParam("sortField", "username").queryParam("sortAsc", "true"))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername("admin").withRoles("ADMIN"))
                .andExpect(request().attribute("sortField", "username"))
                .andExpect(request().attribute("sortAsc", "true"))
                .andExpect(xpath("/html/body/table").exists())
                .andExpect(xpath("/html/body/table/tr[2]/td[1]").string("admin"));
    }

    @Test
    public void testShowAdminPage_SortUsernameDesc() throws Exception {
        this.mockMvc.perform(get("/admin/users/1").queryParam("sortField", "username").queryParam("sortAsc", "false"))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername("admin").withRoles("ADMIN"))
                .andExpect(request().attribute("sortField", "username"))
                .andExpect(request().attribute("sortAsc", "false"))
                .andExpect(xpath("/html/body/table").exists())
                .andExpect(xpath("/html/body/table/tr[2]/td[1]").string("user49"));
    }

}
