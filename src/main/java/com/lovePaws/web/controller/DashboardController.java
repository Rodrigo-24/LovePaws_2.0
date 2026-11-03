package com.lovePaws.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.lovePaws.dashboard.service.AdminDashboardService;
import com.lovePaws.dashboard.service.GestorDashboardService;

import com.lovePaws.mascota.service.MascotaService;

@Controller
public class DashboardController {

	private final AdminDashboardService adminDashboardService;
	private final GestorDashboardService gestorDashboardService;
	private final MascotaService mascotaService;

	public DashboardController(AdminDashboardService adminDashboardService,
			GestorDashboardService gestorDashboardService, MascotaService mascotaService) {
		this.adminDashboardService = adminDashboardService;
		this.gestorDashboardService = gestorDashboardService;
		this.mascotaService = mascotaService;
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
	public String catalogo(Model model) {
		model.addAttribute("mascotas", mascotaService.listarCatalogo());
		return "adoptante/catalogo";
	}
}