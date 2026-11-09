package com.lovePaws.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.lovePaws.exception.DatoDuplicadoException;
import com.lovePaws.exception.DatoInvalidoException;
import com.lovePaws.exception.RecursoNoEncontradoException;
import com.lovePaws.usuario.dto.ActualizarPerfilDTO;
import com.lovePaws.usuario.dto.CambiarPasswordDTO;
import com.lovePaws.usuario.entity.Usuario;
import com.lovePaws.usuario.service.UsuarioService;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class CuentaWebController {

	private final UsuarioService usuarioService;

	public CuentaWebController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	@GetMapping("/adoptante/cuenta")
	public String mostrarCuentaAdoptante(Authentication authentication, Model model) {
		Usuario usuario = obtenerUsuario(authentication.getName());
		prepararModelo(model, usuario, true);
		return "usuario/cuenta";
	}

	// Actualizar nombre de usuario y correo
	@PostMapping("/adoptante/cuenta/credenciales")
	public String actualizarCredencialesAdoptante(@Valid @ModelAttribute("perfil") ActualizarPerfilDTO datos,
			BindingResult resultado, Authentication authentication, Model model, HttpServletRequest request,
			HttpServletResponse response) {
		Usuario usuarioActual = obtenerUsuario(authentication.getName());

		if (resultado.hasErrors()) {
			prepararModelo(model, usuarioActual, true);
			return "usuario/cuenta";
		}
		try {
			Usuario usuarioActualizado = usuarioService.actualizarCredencialesAdoptante(authentication.getName(),
					datos);

			if (!usuarioActualizado.getUsername().equals(authentication.getName())) {
				new SecurityContextLogoutHandler().logout(request, response, authentication);
				return "redirect:/login?usuarioActualizado";
			}
			return "redirect:/adoptante/cuenta?credencialesActualizadas";
			
		} catch (DatoDuplicadoException | DatoInvalidoException ex) {
			resultado.reject("perfil.invalido", ex.getMessage());
			prepararModelo(model, usuarioActual, true);
			return "usuario/cuenta";
		}
	}


	@GetMapping("/gestor/cuenta")
	public String mostrarCuentaGestor(Authentication authentication, Model model) {
		Usuario usuario = obtenerUsuario(authentication.getName());
		prepararModelo(model, usuario, false);
		return "usuario/cuenta";
	}

	@PostMapping("/adoptante/cuenta/password")
	public String cambiarPasswordAdoptante(@Valid @ModelAttribute("cambioPassword") CambiarPasswordDTO datos,
			BindingResult resultado, Authentication authentication, Model model) {
		Usuario usuario = obtenerUsuario(authentication.getName());

		if (resultado.hasErrors()) {
			prepararModelo(model, usuario, true);
			return "usuario/cuenta";
		}
		
		try {
			usuarioService.cambiarPassword(authentication.getName(), datos);
			return "redirect:/adoptante/cuenta?passwordActualizada";

		} catch (DatoInvalidoException ex) {
			resultado.reject("password.invalida", ex.getMessage());
			prepararModelo(model, usuario, true);
			return "usuario/cuenta";
		}
	}


	@PostMapping("/gestor/cuenta/password")
	public String cambiarPasswordGestor(@Valid @ModelAttribute("cambioPassword") CambiarPasswordDTO datos,
			BindingResult resultado, Authentication authentication, Model model) {
		Usuario usuario = obtenerUsuario(authentication.getName());

		if (resultado.hasErrors()) {
			prepararModelo(model, usuario, false);
			return "usuario/cuenta";
		}

		try {
			usuarioService.cambiarPassword(authentication.getName(), datos);
			return "redirect:/gestor/cuenta?passwordActualizada";

		} catch (DatoInvalidoException ex) {
			resultado.reject("password.invalida", ex.getMessage());
			prepararModelo(model, usuario, false);
			return "usuario/cuenta";
		}
	}
	
	private Usuario obtenerUsuario(String username) {
		return usuarioService.buscarPorUsername(username)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario."));
	}

	private void prepararModelo(Model model, Usuario usuario, boolean esAdoptante) {
		model.addAttribute("usuario", usuario);
		model.addAttribute("esAdoptante", esAdoptante);

		if (!model.containsAttribute("cambioPassword")) {
			model.addAttribute("cambioPassword", new CambiarPasswordDTO());
		}
		if (esAdoptante && !model.containsAttribute("perfil")) {

			ActualizarPerfilDTO perfil = new ActualizarPerfilDTO();

			perfil.setUsername(usuario.getUsername());
			perfil.setEmail(usuario.getEmail());

			model.addAttribute("perfil", perfil);
		}
	}
}