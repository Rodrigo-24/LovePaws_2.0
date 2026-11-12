package com.lovePaws.usuario.controller;

import com.lovePaws.usuario.dto.LoginAdoptanteDTO;
import com.lovePaws.usuario.dto.TokenRespuestaDTO;
import com.lovePaws.usuario.service.AutenticacionService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AutenticacionController {

	private final AutenticacionService autenticacionService;

	public AutenticacionController(AutenticacionService autenticacionService) {
		this.autenticacionService = autenticacionService;
	}

	// Iniciar sesión desde la aplicación móvil
	@PostMapping("/login")
	public ResponseEntity<TokenRespuestaDTO> iniciarSesion(@Valid @RequestBody LoginAdoptanteDTO datos) {

		try {
			TokenRespuestaDTO respuesta = autenticacionService.autenticarAdoptante(datos);

			return ResponseEntity.ok(respuesta);

		} catch (AuthenticationException ex) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
}
