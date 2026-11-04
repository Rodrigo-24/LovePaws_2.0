package com.lovePaws.mascota.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.lovePaws.mascota.dto.MascotaRespuestaDTO;
import com.lovePaws.mascota.service.MascotaService;

@RestController
@RequestMapping("/api/admin/mascotas")
@PreAuthorize("hasRole('ADMIN')")
public class MascotaAdminController {

	private final MascotaService mascotaService;

	public MascotaAdminController(MascotaService mascotaService) {
		this.mascotaService = mascotaService;
	}

	// Listar todas las mascotas
	@GetMapping
	public ResponseEntity<List<MascotaRespuestaDTO>> listarTodas() {
		return ResponseEntity.ok(mascotaService.listarTodasLasMascotas());
	}

	// Consultar detalles de cualquier mascota
	@GetMapping("/{idMascota}")
	public ResponseEntity<MascotaRespuestaDTO> buscarPorId(@PathVariable Long idMascota) {
		return ResponseEntity.ok(mascotaService.buscarMascotaAdmin(idMascota));
	}

	@PutMapping("/{id}/bloquear")
	public ResponseEntity<MascotaRespuestaDTO> bloquearMascota(@PathVariable Long id, Authentication authentication) {
		MascotaRespuestaDTO respuesta = mascotaService.bloquearMascota(id, authentication.getName());
		return ResponseEntity.ok(respuesta);
	}

	@PutMapping("/{id}/desbloquear")
	public ResponseEntity<MascotaRespuestaDTO> desbloquearMascota(@PathVariable Long id) {
		MascotaRespuestaDTO respuesta = mascotaService.desbloquearMascota(id);
		return ResponseEntity.ok(respuesta);
	}
}