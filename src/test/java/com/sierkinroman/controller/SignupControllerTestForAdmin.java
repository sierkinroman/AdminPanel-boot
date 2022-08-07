package com.sierkinroman.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import com.sierkinroman.entities.dto.UserSignupDto;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@WithUserDetails(value = "admin")
class SignupControllerTestForAdmin {

	@Autowired
    private MockMvc mockMvc;
	
	@Test
	public void testShowSignup() throws Exception {
		this.mockMvc.perform(get("/admin/addUser"))
			.andExpect(status().isOk())
			.andExpect(authenticated())
			.andExpect(xpath("/html/body/div/form/p[1]").string("Add User"))
			.andExpect(xpath("//div[@id='roles_wrapper']").exists());
	}

	@Test
	public void testCorrectSignup() throws Exception {
		UserSignupDto userSignupDto = new UserSignupDto("admin100",
												"123456",
												"123456",
												"admin100@gmail.com",
												"Admin100",
												"AdminLN100",
												null);
		this.mockMvc.perform(post("/admin/addUser").flashAttr("userSignupDto", userSignupDto).with(csrf()))
			.andExpect(status().is3xxRedirection())
			.andExpect(authenticated())
			.andExpect(view().name("redirect:/"));
	}
	
	@Test
	public void testInvalidSignup() throws Exception {
		UserSignupDto userSignupDto = new UserSignupDto("user1",
												"123456",
												"1234567",
												"user1@gmail.com",
												"User1",
												"UserLN1",
												null);
		this.mockMvc.perform(post("/admin/addUser").flashAttr("userSignupDto", userSignupDto).with(csrf()))
			.andExpect(status().isOk())
			.andExpect(authenticated())
			.andExpect(view().name("signup"))
			.andExpect(xpath("/html/body/div/form/p[3]").string("*Username is used"))
			.andExpect(xpath("/html/body/div/form/p[6]").string("*Confirm password is wrong"))
			.andExpect(xpath("/html/body/div/form/p[8]").string("*E-mail is used"))
			.andExpect(xpath("//div[@id='roles_wrapper']").exists());
	}

}
