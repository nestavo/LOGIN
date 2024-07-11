package com.Security6.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Security6.User.Role;
import com.Security6.User.User;
import com.Security6.User.UserRepository;
import com.Security6.jwt.JwtService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
	

	public AuthResponse login(LoginRequest request) {
		
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		UserDetails user =userRepository.findByUsername(request.getUsername()).orElseThrow();
		
		String token=jwtService.getToken(user);
		
		return AuthResponse.builder()
				.token(token)
				.build();
		
	}

	public AuthResponse register(RegisterRequest request) {

		User user = User.builder().username(request.username).password(request.password).firstname(request.firstname)
				.lastname(request.lastname).country(request.country).role(Role.USER).build();

		userRepository.save(user);
		return AuthResponse.builder()
				.token(jwtService
						.getToken(user)).build();

	}
}