package com.lovePaws.web.controller;

import java.util.Locale;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lovePaws.dashboard.service.AdminDashboardService;
import com.lovePaws.dashboard.service.GestorDashboardService;

import com.lovePaws.mascota.service.MascotaService;
import com.lovePaws.solicitud.service.SolicitudAdopcionService;

@Controller
public class DashboardController {

	private final AdminDashboardService adminDashboardService;
	private final GestorDashboardService gestorDashboardService;
	private final MascotaService mascotaService;
	private final SolicitudAdopcionService solicitudAdopcionService;

	public DashboardController(AdminDashboardService adminDashboardService,
			GestorDashboardService gestorDashboardService, MascotaService mascotaService,
			SolicitudAdopcionService solicitudAdopcionService) {
		this.adminDashboardService = adminDashboardService;
		this.gestorDashboardService = gestorDashboardService;
		this.mascotaService = mascotaService;
		this.solicitudAdopcionService = solicitudAdopcionService;
	}

	@GetMapping("/admin/dashboard")
	public String adminDashboard(Model model) {
		model.addAttribute("resumen", adminDashboardService.obtenerResumen());
		return "admin/dashboard";
	}

	@GetMapping("/gestor/dashboard")
	public String gestorDashboard(Authentication authentication, Model model) {
		model.addAttribute("resumen", gestorDashboardService.obtenerResumen(authentication.getName()));
		return "gestor/dashboard";
	}

	@GetMapping("/adoptante/catalogo")
	public String catalogo(@RequestParam(required = false) String tipo, Model model) {
		String tipoSeleccionado = (tipo == null || tipo.isBlank()) ? "TODOS" : tipo.trim().toUpperCase(Locale.ROOT);
		model.addAttribute("mascotas", mascotaService.listarCatalogo(tipoSeleccionado.equals("TODOS") ? null : tipoSeleccionado));
		model.addAttribute("tipoSeleccionado", tipoSeleccionado);
		return "adoptante/catalogo";
	}

	@GetMapping("/adoptante/catalogo/resultados")
	public String resultadosCatalogo(@RequestParam(required = false) String tipo, Model model) {
		String tipoSeleccionado = (tipo == null || tipo.isBlank()) ? "TODOS" : tipo.trim().toUpperCase(Locale.ROOT);
		model.addAttribute("mascotas", mascotaService.listarCatalogo(tipoSeleccionado.equals("TODOS") ? null : tipoSeleccionado));
		model.addAttribute("tipoSeleccionado", tipoSeleccionado);
		return "adoptante/catalogo :: resultados";
	}

	@GetMapping("/adoptante/nosotros")
	public String nosotros() {
		return "adoptante/nosotros";
	}

	@GetMapping("/adoptante/mis-adopciones")
	public String misAdopciones(Authentication authentication, Model model) {
		model.addAttribute("solicitudes",
				solicitudAdopcionService.listarSolicitudesAdoptante(authentication.getName()));
		return "adoptante/mis-adopciones";
	}
}