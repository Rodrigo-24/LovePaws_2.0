package com.lovePaws.security;

import java.io.IOException;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.lovePaws.usuario.entity.EstadoUsuario;
import com.lovePaws.usuario.repository.UsuarioRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CuentaActivaFilter extends OncePerRequestFilter {

	private final UsuarioRepository usuarioRepository;

	public CuentaActivaFilter(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// Comprobar únicamente usuarios autenticados
		if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {

			// Consultar si la cuenta continúa activa
			boolean cuentaActiva = usuarioRepository.findByUsernameAndEstado(authentication.getName(), EstadoUsuario.ACTIVO).isPresent();

			// Cerrar la sesión si la cuenta fue bloqueada
			if (!cuentaActiva) {
				SecurityContextHolder.clearContext();
				HttpSession session = request.getSession(false);
				if (session != null) {
					session.invalidate();
				}
				response.sendRedirect(request.getContextPath() + "/login?blocked");
				return;
			}
		}
		filterChain.doFilter(request, response);
	}
}