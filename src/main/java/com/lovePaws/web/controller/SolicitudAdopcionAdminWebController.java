package com.lovePaws.web.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.lovePaws.solicitud.dto.EventoAdopcionDTO;
import com.lovePaws.solicitud.dto.SolicitudAdopcionAdminDTO;
import com.lovePaws.solicitud.dto.SolicitudAdopcionDetalleDTO;
import com.lovePaws.solicitud.entity.EstadoSolicitud;
import com.lovePaws.solicitud.entity.TipoEventoAdopcion;
import com.lovePaws.solicitud.service.SolicitudAdopcionService;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class SolicitudAdopcionAdminWebController {

	private final SolicitudAdopcionService solicitudService;

	public SolicitudAdopcionAdminWebController(SolicitudAdopcionService solicitudService) {
		this.solicitudService = solicitudService;
	}

	// Mostrar la bandeja administrativa de solicitudes
	@GetMapping("/admin/solicitudes")
	public String listarSolicitudes(@RequestParam(required = false) EstadoSolicitud estado, Model model) {

		List<SolicitudAdopcionAdminDTO> solicitudes = solicitudService.listarSolicitudesAdmin(estado);
		model.addAttribute("solicitudes", solicitudes);
		model.addAttribute("estadoSeleccionado", estado != null ? estado.name() : "");
		model.addAttribute("estados", EstadoSolicitud.values());

		return "admin/solicitudes/listado";
	}

	// Mostrar el detalle administrativo de una solicitud
	@GetMapping("/admin/solicitudes/{id}/detalle")
	public String verDetalleSolicitud(@PathVariable Long id, Model model) {

		// Obtener la información principal
		SolicitudAdopcionDetalleDTO solicitud = solicitudService.obtenerDetalleSolicitudAdmin(id);

		// Obtener el historial de eventos
		List<EventoAdopcionDTO> historial = solicitudService.obtenerHistorialAdmin(id);
		model.addAttribute("solicitud", solicitud);
		model.addAttribute("historial", historial);

		return "admin/solicitudes/detalle";
	}

	// Mostrar el historial general de adopciones
	@GetMapping("/admin/historial")
	public String verHistorialGeneral(@RequestParam(required = false) TipoEventoAdopcion tipoEvento,
			@RequestParam(defaultValue = "0") int pagina, Model model) {

		// Consultar los eventos paginados
		Page<EventoAdopcionDTO> historial = solicitudService.listarHistorialGeneral(tipoEvento, PageRequest.of(Math.max(pagina, 0), 10));
		model.addAttribute("historial", historial);
		model.addAttribute("tiposEvento", TipoEventoAdopcion.values());
		model.addAttribute("tipoSeleccionado", tipoEvento != null ? tipoEvento.name() : "");

		return "admin/historial/listado";
	}
}