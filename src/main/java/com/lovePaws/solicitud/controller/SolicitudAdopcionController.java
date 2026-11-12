package com.lovePaws.solicitud.controller;

import com.lovePaws.solicitud.dto.CrearSolicitudDTO;
import com.lovePaws.solicitud.dto.PreguntaSolicitudDTO;
import com.lovePaws.solicitud.dto.SolicitudAdopcionDetalleDTO;
import com.lovePaws.solicitud.dto.SolicitudAdopcionRespuestaDTO;
import com.lovePaws.solicitud.service.SolicitudAdopcionService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/adoptante/solicitudes")
@PreAuthorize("hasRole('ADOPTANTE')")
public class SolicitudAdopcionController {

	private final SolicitudAdopcionService solicitudService;

	public SolicitudAdopcionController(SolicitudAdopcionService solicitudService) {
		this.solicitudService = solicitudService;
	}

	// Registrar una solicitud de adopción
	@PostMapping
	public ResponseEntity<SolicitudAdopcionRespuestaDTO> crearSolicitud(@Valid @RequestBody CrearSolicitudDTO datos,
			Authentication authentication) {
		SolicitudAdopcionRespuestaDTO respuesta = solicitudService.crearSolicitud(datos, authentication.getName());
		return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
	}

	// Consultar las solicitudes del adoptante autenticado
	@GetMapping
	public ResponseEntity<List<SolicitudAdopcionRespuestaDTO>> listarSolicitudes(Authentication authentication) {
		List<SolicitudAdopcionRespuestaDTO> solicitudes = solicitudService
				.listarSolicitudesAdoptante(authentication.getName());
		return ResponseEntity.ok(solicitudes);
	}

	// Consultar el detalle de una solicitud
	@GetMapping("/{id}")
	public ResponseEntity<SolicitudAdopcionDetalleDTO> obtenerDetalleSolicitud(@PathVariable Long id,
			Authentication authentication) {
		SolicitudAdopcionDetalleDTO respuesta = solicitudService.obtenerDetalleSolicitud(id, authentication.getName());
		return ResponseEntity.ok(respuesta);
	}

	// Consultar las preguntas activas del formulario de adopción
	@GetMapping("/preguntas")
	public ResponseEntity<List<PreguntaSolicitudDTO>> listarPreguntasActivas() {
		List<PreguntaSolicitudDTO> preguntas = solicitudService.listarPreguntasActivas();
		return ResponseEntity.ok(preguntas);
	}
}