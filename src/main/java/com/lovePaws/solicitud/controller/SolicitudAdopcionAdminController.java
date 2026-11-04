package com.lovePaws.solicitud.controller;

import com.lovePaws.solicitud.dto.EventoAdopcionDTO;
import com.lovePaws.solicitud.entity.TipoEventoAdopcion;
import com.lovePaws.solicitud.service.SolicitudAdopcionService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/solicitudes")
@PreAuthorize("hasRole('ADMIN')")
public class SolicitudAdopcionAdminController {

	private final SolicitudAdopcionService solicitudService;

	public SolicitudAdopcionAdminController(SolicitudAdopcionService solicitudService) {
		this.solicitudService = solicitudService;
	}

	// Consultar el historial general con filtro opcional
	@GetMapping("/eventos")
	public ResponseEntity<Page<EventoAdopcionDTO>> listarHistorialGeneral(@RequestParam(required = false) TipoEventoAdopcion tipoEvento, Pageable pageable) {
		Page<EventoAdopcionDTO> historial = solicitudService.listarHistorialGeneral(tipoEvento, pageable);
		return ResponseEntity.ok(historial);
	}
}