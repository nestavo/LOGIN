package com.Security6.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.Security6.User.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
	
	private final UserRepository userRepository;
	
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		
		return config.getAuthenticationManager();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		
		authenticationProvider.setUserDetailsService(UserDetailsService());
		
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		
		return authenticationProvider;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		// TODO Auto-generated method stub
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService UserDetailsService() {
		// TODO Auto-generated method stub
		return username -> userRepository.findByUsername(username)
				.orElseThrow(()-> new UsernameNotFoundException("Usuario no encontrado"));
	}
} 
