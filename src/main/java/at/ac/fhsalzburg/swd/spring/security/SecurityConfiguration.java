package at.ac.fhsalzburg.swd.spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {
	
	
	@Autowired
	private TokenService tokenService;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// order is important, start with most specific
		http.csrf().and().cors().and()
		// JWT Authentication
		.addFilterBefore(new JwtAuthenticationFilter(tokenService),
				UsernamePasswordAuthenticationFilter.class)
	    .authorizeRequests()
	    	.antMatchers("/api/**") 
			.hasAnyRole("USER", "ADMIN") // everything under /api needs either user or admin role
	    	.antMatchers("/admin/**")
			.hasAnyRole("ADMIN") // everything under /admin needs admin role
			.antMatchers("/user/**")
			.hasAnyRole("USER", "ADMIN") // everything under /user needs either user or admin role		
	    .and().csrf().disable().formLogin() // add form based authentication
	    	.loginPage("/login") // our login page
	    	.defaultSuccessUrl("/") // where to go when login was successful
	    	.failureUrl("/login-error")
	    .and().csrf().disable().logout()    	
    		.logoutSuccessUrl("/") // where to go after logout
    		.invalidateHttpSession(true) // at logout invalidate session
    		.deleteCookies("JSESSIONID") // and delete session cookie
      	;
		
		http.headers().frameOptions().disable(); // h2-console would not work otherwise
		
	    return http.build();		
		
	}
	
	
	
	
}