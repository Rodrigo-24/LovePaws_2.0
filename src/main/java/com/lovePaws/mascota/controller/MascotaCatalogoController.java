package com.lovePaws.mascota.controller;

import com.lovePaws.mascota.dto.MascotaRespuestaDTO;
import com.lovePaws.mascota.service.MascotaService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/adoptante/mascotas")
@PreAuthorize("hasRole('ADOPTANTE')")
public class MascotaCatalogoController {

	private final MascotaService mascotaService;

	public MascotaCatalogoController(MascotaService mascotaService) {
		this.mascotaService = mascotaService;
	}

	// Consultar el catálogo de mascotas con filtro opcional
	@GetMapping
	public ResponseEntity<List<MascotaRespuestaDTO>> listarCatalogo(@RequestParam(required = false) String tipo) {
		List<MascotaRespuestaDTO> mascotas = mascotaService.listarCatalogo(tipo);
		return ResponseEntity.ok(mascotas);
	}

	// Consultar el detalle de una mascota disponible
	@GetMapping("/{idMascota}")
	public ResponseEntity<MascotaRespuestaDTO> obtenerDetalleMascota(@PathVariable Long idMascota) {
		MascotaRespuestaDTO mascota = mascotaService.buscarMascotaDisponible(idMascota);
		return ResponseEntity.ok(mascota);
	}
}