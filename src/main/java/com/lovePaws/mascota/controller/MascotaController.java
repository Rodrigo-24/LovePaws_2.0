package com.lovePaws.mascota.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.lovePaws.mascota.dto.RegistrarMascotaDTO;
import com.lovePaws.mascota.dto.ActualizarMascotaDTO;
import com.lovePaws.mascota.dto.MascotaRespuestaDTO;
import com.lovePaws.mascota.service.MascotaService;

@RestController
@RequestMapping("/api/gestor/mascotas")
public class MascotaController {

	private final MascotaService mascotaService;

	public MascotaController(MascotaService mascotaService) {
		this.mascotaService = mascotaService;
	}

	@PostMapping
	@PreAuthorize("hasRole('GESTOR')")
	public ResponseEntity<MascotaRespuestaDTO> registrarMascota(@Valid @RequestBody RegistrarMascotaDTO datos,
			Authentication authentication) {
		MascotaRespuestaDTO respuesta = mascotaService.registrarMascota(datos, authentication.getName());
		return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
	}

	@PutMapping("/{idMascota}")
	@PreAuthorize("hasRole('GESTOR')")
	public ResponseEntity<MascotaRespuestaDTO> actualizarMascota(@PathVariable Long idMascota,
			@Valid @RequestBody ActualizarMascotaDTO datos, Authentication authentication) {
		MascotaRespuestaDTO respuesta = mascotaService.actualizarMascota(idMascota, datos, authentication.getName());
		return ResponseEntity.ok(respuesta);
	}

	@GetMapping
	@PreAuthorize("hasRole('GESTOR')")
	public ResponseEntity<List<MascotaRespuestaDTO>> listarMascotas(Authentication authentication) {
		List<MascotaRespuestaDTO> mascotas = mascotaService.listarMascotasGestor(authentication.getName());
		return ResponseEntity.ok(mascotas);
	}

	@GetMapping("/{idMascota}")
	@PreAuthorize("hasRole('GESTOR')")
	public ResponseEntity<MascotaRespuestaDTO> buscarMascota(@PathVariable Long idMascota, 
			Authentication authentication) { 
		MascotaRespuestaDTO respuesta = mascotaService.buscarMascotaGestor(idMascota, authentication.getName());
		return ResponseEntity.ok(respuesta);
	}
}
