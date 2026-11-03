package com.lovePaws.dashboard.service;

import org.springframework.stereotype.Service;

import com.lovePaws.dashboard.dto.AdminDashboardDTO;
import com.lovePaws.dashboard.repository.AdminDashboardRepository;

@Service
public class AdminDashboardService {

	private final AdminDashboardRepository dashboardRepository;

	public AdminDashboardService(AdminDashboardRepository dashboardRepository) {
		this.dashboardRepository = dashboardRepository;
	}

	public AdminDashboardDTO obtenerResumen() {
		long totalUsuarios = dashboardRepository.contarUsuarios();
		long totalMascotas = dashboardRepository.contarMascotas();
		long solicitudesPendientes = dashboardRepository.contarSolicitudesPendientes();
		return new AdminDashboardDTO(totalUsuarios, totalMascotas, solicitudesPendientes);
	}
}