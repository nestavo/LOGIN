package es.nestavo.zlogin.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import es.nestavo.zlogin.servicio.UsuarioService;
@EnableTransactionManagement
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private UsuarioService usuarioService;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
		        //.authorizeHttpRequests(auth -> auth.requestMatchers("/recuperaContra").permitAll()
			    	.authorizeHttpRequests(auth -> auth.requestMatchers("/registro",
			    			"/recuperarcontra/**",
			    			"/reset/**").permitAll()
						.requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/user/**").hasRole("USER")
						.anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/login").failureUrl("/login?error=true").permitAll()
						.successHandler(customAuthenticationSuccessHandler()))
				.logout(logout -> logout.permitAll()
						);
		return http.build();

	}
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//        return http.getSharedObject(AuthenticationManagerBuilder.class)
//            .userDetailsService(usuarioService)
//            .passwordEncoder(passwordEncoder())  
//            .and()
//            .build();
//    }

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
		return new CustomAuthenticationSuccessHandler();
	}

}
