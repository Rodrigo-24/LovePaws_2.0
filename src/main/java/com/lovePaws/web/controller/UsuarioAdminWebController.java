package com.lovePaws.web.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lovePaws.usuario.dto.UsuarioRespuestaDTO;
import com.lovePaws.usuario.entity.EstadoUsuario;
import com.lovePaws.usuario.entity.RolNombre;
import com.lovePaws.usuario.service.UsuarioService;
import com.lovePaws.exception.DatoInvalidoException;
import com.lovePaws.exception.RecursoNoEncontradoException;

@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioAdminWebController {

	private final UsuarioService usuarioService;

	public UsuarioAdminWebController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	@GetMapping
	public String listarUsuarios(@RequestParam(required = false) RolNombre rol,
			@RequestParam(required = false) EstadoUsuario estado, Model model) {

		List<UsuarioRespuestaDTO> usuarios = usuarioService.listarUsuariosGestionables(rol, estado);

		model.addAttribute("usuarios", usuarios);
		model.addAttribute("rolSeleccionado", rol != null ? rol.name() : "");
		model.addAttribute("estadoSeleccionado", estado != null ? estado.name() : "");
		model.addAttribute("totalUsuarios", usuarios.size());

		return "admin/usuarios/listado";
	}

	@PostMapping("/{id}/bloquear")
	public String bloquearUsuario(@PathVariable Long id, @RequestParam String motivo, Authentication authentication,
			RedirectAttributes redirectAttributes) {
		try {
			usuarioService.bloquearUsuario(id, authentication.getName(), motivo);
			redirectAttributes.addFlashAttribute("mensajeExito", "La cuenta fue bloqueada correctamente.");

		} catch (DatoInvalidoException | RecursoNoEncontradoException e) {
			redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
		}

		return "redirect:/admin/usuarios";
	}

	@PostMapping("/{id}/desbloquear")
	public String desbloquearUsuario(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
		try {
			usuarioService.desbloquearUsuario(id, authentication.getName());
			redirectAttributes.addFlashAttribute("mensajeExito", "La cuenta fue desbloqueada correctamente.");

		} catch (DatoInvalidoException | RecursoNoEncontradoException e) {
			redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
		}

		return "redirect:/admin/usuarios";
	}
}