package com.sierkinroman;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
public class LoginTest {
	
	@Autowired
    private MockMvc mockMvc;

	@Test
	public void accessDeniedTest() throws Exception {
		this.mockMvc.perform(get("/"))
			.andExpect(status().is3xxRedirection())
			.andExpect(unauthenticated())
			.andExpect(redirectedUrl("http://localhost/login"));		
	}
	
	@Test
	public void correctLoginTest() throws Exception {
		this.mockMvc.perform(formLogin().user("admin").password("admin"))
			.andExpect(status().is3xxRedirection())
			.andExpect(authenticated().withUsername("admin").withRoles("ADMIN"))
			.andExpect(redirectedUrl("/"));
	}
	
	@Test
	public void incorrectLoginTest() throws Exception {
		this.mockMvc.perform(formLogin().user("admin12345").password("admin"))
			.andExpect(status().is3xxRedirection())
			.andExpect(unauthenticated())
			.andExpect(redirectedUrl("/login?error=true"));
	}
	
	@Test
	public void incorrectPasswordTest() throws Exception {
		this.mockMvc.perform(formLogin().user("admin").password("admin123"))
			.andExpect(status().is3xxRedirection())
			.andExpect(unauthenticated())
			.andExpect(redirectedUrl("/login?error=true"));
	}
	
	@Test
	public void emptyCredentialsTest() throws Exception {
		this.mockMvc.perform(formLogin().user("").password(""))
			.andExpect(status().is3xxRedirection())
			.andExpect(unauthenticated())
			.andExpect(redirectedUrl("/login?error=true"));
	}

}
