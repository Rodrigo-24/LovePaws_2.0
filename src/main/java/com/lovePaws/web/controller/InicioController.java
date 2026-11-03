package com.lovePaws.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

	@GetMapping("/")
	public String inicio(Authentication authentication) {

		boolean esAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

		boolean esGestor = authentication.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_GESTOR"));

		boolean esAdoptante = authentication.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_ADOPTANTE"));

		if (esAdmin) {
			return "redirect:/admin/dashboard";
		}

		if (esGestor) {
			return "redirect:/gestor/dashboard";
		}

		if (esAdoptante) {
			return "redirect:/adoptante/catalogo";
		}

		return "redirect:/login";
	}
}