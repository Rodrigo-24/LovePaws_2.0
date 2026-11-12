package com.lovePaws.usuario.controller;

import com.lovePaws.exception.RecursoNoEncontradoException;
import com.lovePaws.usuario.dto.ActualizarPerfilDTO;
import com.lovePaws.usuario.dto.CambiarPasswordDTO;
import com.lovePaws.usuario.dto.UsuarioRespuestaDTO;
import com.lovePaws.usuario.entity.Usuario;
import com.lovePaws.usuario.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/adoptante/perfil")
@PreAuthorize("hasRole('ADOPTANTE')")
public class AdoptantePerfilController {

	private final UsuarioService usuarioService;

	public AdoptantePerfilController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	// Consultar el perfil del adoptante autenticado
	@GetMapping
	public ResponseEntity<UsuarioRespuestaDTO> obtenerPerfil(Authentication authentication) {
		Usuario usuario = usuarioService.buscarPorUsername(authentication.getName())
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario autenticado."));

		return ResponseEntity.ok(convertirARespuesta(usuario));
	}

	// Actualizar nombre de usuario y correo electrónico
	@PutMapping
	public ResponseEntity<UsuarioRespuestaDTO> actualizarPerfil(@Valid @RequestBody ActualizarPerfilDTO datos, Authentication authentication) {
		Usuario usuario = usuarioService.actualizarCredencialesAdoptante(authentication.getName(), datos);

		return ResponseEntity.ok(convertirARespuesta(usuario));
	}

	// Cambiar la contraseña
	@PutMapping("/password")
	public ResponseEntity<Void> cambiarPassword(@Valid @RequestBody CambiarPasswordDTO datos, Authentication authentication) {
		usuarioService.cambiarPassword(authentication.getName(), datos);

		return ResponseEntity.noContent().build();
	}

	// Convertir la entidad Usuario en un DTO seguro
	private UsuarioRespuestaDTO convertirARespuesta(Usuario usuario) {

		return new UsuarioRespuestaDTO(usuario.getIdUsuario(), usuario.getUsername(), usuario.getEmail(),
				usuario.getNombres(), usuario.getApellidos(), usuario.getFotoPerfilUrl(), usuario.getRol().getNombre(),
				usuario.getEstado(), usuario.getFechaCreacion());
	}
}
