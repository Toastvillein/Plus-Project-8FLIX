package com.example.eightflix.global.security;

import com.example.eightflix.domain.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtFilter jwtFilter;
	private final CustomAccessDeniedHandler accessDeniedHandler;
	private final RefreshTokenFilter refreshTokenFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http
				.csrf(csrf -> csrf.disable())
				.exceptionHandling(ex -> ex
						.accessDeniedHandler(accessDeniedHandler)
				)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(SecurityUrlMatcher.REFRESH_URL).authenticated()
						.requestMatchers(SecurityUrlMatcher.PUBLIC_URLS).permitAll()
						.requestMatchers(SecurityUrlMatcher.ADMIN_URLS).hasRole("ADMIN")
						.anyRequest().authenticated()
				)
				.addFilterBefore(refreshTokenFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

}
