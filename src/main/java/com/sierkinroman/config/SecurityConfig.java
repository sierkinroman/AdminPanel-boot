package com.sierkinroman.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.sierkinroman.service.impl.userdetails.UserDetailsServiceImpl;



@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;
	
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
	

	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
        	.userDetailsService(userDetailsServiceImpl)
        	.passwordEncoder(bCryptPasswordEncoder());
    }
	
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		// TODO configure correctly 
		httpSecurity
			.authorizeRequests()
            	.antMatchers("/css/**","/js/**", "/images/**").permitAll()
//				.antMatchers("/", "/logout").hasAnyRole("USER", "ADMIN")
				.antMatchers("/admin/**").hasRole("ADMIN")
				.antMatchers("/signup", "/login").anonymous()
				.anyRequest().authenticated()
			.and()
				.formLogin()
				.loginPage("/login")
				.defaultSuccessUrl("/", true)
				.failureUrl("/login?error=true")
			.and()
				.logout()
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login")
		;
	}

}
