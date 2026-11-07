package com.lovePaws.web.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lovePaws.solicitud.dto.SolicitudAdopcionDetalleDTO;
import com.lovePaws.solicitud.dto.SolicitudAdopcionGestorDTO;
import com.lovePaws.solicitud.entity.EstadoSolicitud;
import com.lovePaws.solicitud.service.SolicitudAdopcionService;

import jakarta.validation.Valid;

import com.lovePaws.exception.DatoInvalidoException;
import com.lovePaws.exception.RecursoNoEncontradoException;
import com.lovePaws.solicitud.dto.ResolverSolicitudDTO;

@Controller
@PreAuthorize("hasRole('GESTOR')")
public class SolicitudAdopcionWebController {

	private final SolicitudAdopcionService solicitudService;

	public SolicitudAdopcionWebController(SolicitudAdopcionService solicitudService) {
		this.solicitudService = solicitudService;
	}

	// Mostrar la bandeja de solicitudes
	@GetMapping("/gestor/solicitudes")
	public String listarSolicitudes(@RequestParam(required = false) EstadoSolicitud estado,
			Authentication authentication, Model model) {
		String username = authentication.getName();
		List<SolicitudAdopcionGestorDTO> solicitudes;

		// 1. Consultar todas las solicitudes o filtrar por estado
		if (estado != null) {
			solicitudes = solicitudService.listarSolicitudesGestor(username, estado);
		} else {
			solicitudes = new ArrayList<>();
			for (EstadoSolicitud estadoSolicitud : EstadoSolicitud.values()) {
				solicitudes.addAll(solicitudService.listarSolicitudesGestor(username, estadoSolicitud));
			}

			// Ordenar de la más reciente a la más antigua
			solicitudes.sort(Comparator.comparing(SolicitudAdopcionGestorDTO::getFechaSolicitud).reversed());
		}

		// 2. Enviar información a Thymeleaf
		model.addAttribute("solicitudes", solicitudes);
		model.addAttribute("estadoSeleccionado", estado);
		model.addAttribute("estados", EstadoSolicitud.values());
		return "gestor/solicitudes/listado";
	}

	// Mostrar el detalle de una solicitud
	@GetMapping("/gestor/solicitudes/{id}/detalle")
	public String verDetalleSolicitud(@PathVariable Long id, Authentication authentication, Model model) {
		SolicitudAdopcionDetalleDTO solicitud = solicitudService.obtenerDetalleSolicitudGestor(id,
				authentication.getName());
		model.addAttribute("solicitud", solicitud);
		model.addAttribute("datos", new ResolverSolicitudDTO());
		return "gestor/solicitudes/detalle";
	}

	// Aprobar una solicitud
	@PostMapping("/gestor/solicitudes/{id}/aprobar")
	public String aprobarSolicitud(@PathVariable Long id, @Valid @ModelAttribute("datos") ResolverSolicitudDTO datos,
			BindingResult bindingResult, Authentication authentication, RedirectAttributes redirectAttributes) {
		String rutaDetalle = "redirect:/gestor/solicitudes/" + id + "/detalle";

		// 1. Validar los datos recibidos
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("mensajeError", bindingResult.getFieldError().getDefaultMessage());
			return rutaDetalle;
		}
		try {
			// 2. Ejecutar la aprobación
			solicitudService.aprobarSolicitud(id, datos, authentication.getName());

			// 3. Comunicar el resultado
			redirectAttributes.addFlashAttribute("mensajeExito",
					"Solicitud aprobada correctamente. La mascota ahora "
							+ "figura como adoptada y las demás solicitudes "
							+ "pendientes fueron rechazadas automáticamente.");

		} catch (DatoInvalidoException ex) {
			redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
		} catch (RecursoNoEncontradoException ex) {
			redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
			return "redirect:/gestor/solicitudes";
		}
		return rutaDetalle;
	}

	// Rechazar una solicitud
	@PostMapping("/gestor/solicitudes/{id}/rechazar")
	public String rechazarSolicitud(@PathVariable Long id, @Valid @ModelAttribute("datos") ResolverSolicitudDTO datos,
			BindingResult bindingResult, Authentication authentication, RedirectAttributes redirectAttributes) {
		String rutaDetalle = "redirect:/gestor/solicitudes/" + id + "/detalle";

		// 1. Validar los datos recibidos
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("mensajeError", bindingResult.getFieldError().getDefaultMessage());
			return rutaDetalle;
		}
		try {
			// 2. Ejecutar el rechazo
			solicitudService.rechazarSolicitud(id, datos, authentication.getName());

			// 3. Comunicar el resultado
			redirectAttributes.addFlashAttribute("mensajeExito", "La solicitud fue rechazada correctamente.");
		} catch (DatoInvalidoException ex) {
			redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
		} catch (RecursoNoEncontradoException ex) {
			redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
			return "redirect:/gestor/solicitudes";
		}
		return rutaDetalle;
	}
}