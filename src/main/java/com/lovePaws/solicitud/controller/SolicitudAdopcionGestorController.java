package com.lovePaws.solicitud.controller;

import com.lovePaws.solicitud.dto.ResolverSolicitudDTO;
import com.lovePaws.solicitud.dto.SolicitudAdopcionGestorDTO;
import com.lovePaws.solicitud.dto.SolicitudAdopcionRespuestaDTO;
import com.lovePaws.solicitud.entity.EstadoSolicitud;
import com.lovePaws.solicitud.service.SolicitudAdopcionService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gestor/solicitudes")
@PreAuthorize("hasRole('GESTOR')")
public class SolicitudAdopcionGestorController {

	private final SolicitudAdopcionService solicitudService;

	public SolicitudAdopcionGestorController(SolicitudAdopcionService solicitudService) {
		this.solicitudService = solicitudService;
	}

	// Consultar solicitudes de las mascotas del gestor
	@GetMapping
	public ResponseEntity<List<SolicitudAdopcionGestorDTO>> listarSolicitudes(@RequestParam EstadoSolicitud estado,
			Authentication authentication) {
		List<SolicitudAdopcionGestorDTO> solicitudes = solicitudService
				.listarSolicitudesGestor(authentication.getName(), estado);
		return ResponseEntity.ok(solicitudes);
	}

	// Aprobar una solicitud de adopción
	@PutMapping("/{id}/aprobar")
	public ResponseEntity<SolicitudAdopcionRespuestaDTO> aprobarSolicitud(@PathVariable Long id,
			@Valid @RequestBody ResolverSolicitudDTO datos, Authentication authentication) {
		SolicitudAdopcionRespuestaDTO respuesta = solicitudService.aprobarSolicitud(id, datos,
				authentication.getName());
		return ResponseEntity.ok(respuesta);
	}

	// Rechazar una solicitud de adopción
	@PutMapping("/{id}/rechazar")
	public ResponseEntity<SolicitudAdopcionRespuestaDTO> rechazarSolicitud(@PathVariable Long id,
			@Valid @RequestBody ResolverSolicitudDTO datos, Authentication authentication) {
		SolicitudAdopcionRespuestaDTO respuesta = solicitudService.rechazarSolicitud(id, datos, authentication.getName());
		return ResponseEntity.ok(respuesta);
	}
}