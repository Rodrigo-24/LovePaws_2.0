package com.lovePaws.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	// Autenticacion de la API
	@Bean
	@Order(1)
	public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http,
			JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
		http.securityMatcher("/api/**").csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(
						auth -> auth.requestMatchers(HttpMethod.POST, "/api/auth/registro", "/api/auth/login")
								.permitAll().anyRequest().authenticated())
				.oauth2ResourceServer(
						oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));

		return http.build();
	}

	// Proveedor de autenticacion
	@Bean
	public AuthenticationProvider authenticationProvider(UsuarioDetailsService usuarioDetailsService,
			PasswordEncoder passwordEncoder) {

		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(usuarioDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}

	// Autenticacion de la aplicacion web mediante sesiones
	@Bean
	@Order(2)
	public SecurityFilterChain webSecurityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider,
			CuentaActivaFilter cuentaActivaFilter) throws Exception {

		http.authenticationProvider(authenticationProvider)
				.addFilterBefore(cuentaActivaFilter, UsernamePasswordAuthenticationFilter.class)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/login", "/registro", "/error", "/css/**", "/js/**", "/images/**", "/uploads/mascotas/**")
						.permitAll().requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/gestor/**")
						.hasRole("GESTOR").requestMatchers("/adoptante/**").hasRole("ADOPTANTE").anyRequest()
						.authenticated())
				.formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login").defaultSuccessUrl("/", true)
						.failureUrl("/login?error").permitAll())
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout")
						.invalidateHttpSession(true).clearAuthentication(true).deleteCookies("JSESSIONID").permitAll());

		return http.build();
	}

	@Bean
	public FilterRegistrationBean<CuentaActivaFilter> cuentaActivaFilterRegistration(CuentaActivaFilter filter) {
		FilterRegistrationBean<CuentaActivaFilter> registration = new FilterRegistrationBean<>(filter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
}