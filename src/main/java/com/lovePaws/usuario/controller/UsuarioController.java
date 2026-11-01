package com.lovePaws.usuario.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lovePaws.usuario.dto.RegistroAdoptanteDTO;
import com.lovePaws.usuario.dto.UsuarioRespuestaDTO;
import com.lovePaws.usuario.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class UsuarioController {

	private final UsuarioService usuarioService;

	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}
	 
	@PostMapping("/registro")
	public ResponseEntity<UsuarioRespuestaDTO> registrarAdoptante(@Valid @RequestBody RegistroAdoptanteDTO datos) {
		UsuarioRespuestaDTO respuesta = usuarioService.registrarAdoptante(datos);
		return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
	}
}
