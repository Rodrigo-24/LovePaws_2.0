package com.lovePaws.usuario.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lovePaws.usuario.dto.CrearGestorDTO;
import com.lovePaws.usuario.dto.UsuarioRespuestaDTO;
import com.lovePaws.usuario.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/usuarios")
public class UsuarioAdminController {

	private final UsuarioService usuarioService;
	
	public UsuarioAdminController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}
	
	@PostMapping("/gestores")
    @PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UsuarioRespuestaDTO> registrarGestor(@Valid @RequestBody CrearGestorDTO datos) {
		UsuarioRespuestaDTO respuesta = usuarioService.registrarGestor(datos);
		return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
	}
}
