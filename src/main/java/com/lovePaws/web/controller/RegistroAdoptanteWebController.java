package com.lovePaws.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lovePaws.exception.DatoDuplicadoException;
import com.lovePaws.usuario.dto.RegistroAdoptanteDTO;
import com.lovePaws.usuario.service.UsuarioService;

import jakarta.validation.Valid;

@Controller
public class RegistroAdoptanteWebController {

	private final UsuarioService usuarioService;

	public RegistroAdoptanteWebController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	// Mostrar formulario de registro
	@GetMapping("/registro")
	public String mostrarFormulario(Model model) {
		model.addAttribute("registro", new RegistroAdoptanteDTO());
		return "auth/registro";
	}

	// Procesar registro de adoptante
	@PostMapping("/registro")
	public String registrar(@Valid @ModelAttribute("registro") RegistroAdoptanteDTO datos, BindingResult resultado,
			@RequestParam(name = "confirmarPassword", required = false) String confirmarPassword) {

		// Validar confirmación de contraseña
		if (confirmarPassword == null || confirmarPassword.isBlank()) {
			resultado.rejectValue("password", "password.confirmacion", "Debes confirmar tu contraseña.");
		} else if (datos.getPassword() != null && !datos.getPassword().equals(confirmarPassword)) {
			resultado.rejectValue("password", "password.no.coincide", "Las contraseñas no coinciden.");
		}

		// Comprobar errores de validación
		if (resultado.hasErrors()) {
			return "auth/registro";
		}
		try {
			usuarioService.registrarAdoptante(datos);
		} catch (DatoDuplicadoException ex) {
			resultado.reject("registro.duplicado", ex.getMessage());
			return "auth/registro";
		}

		// Redirigir al login después del registro
		return "redirect:/login?registro";
	}
}