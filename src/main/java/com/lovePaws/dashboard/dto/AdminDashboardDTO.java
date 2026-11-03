package com.lovePaws.dashboard.dto;

public class AdminDashboardDTO {

	private long totalUsuarios;
	private long totalMascotas;
	private long solicitudesPendientes;

	public AdminDashboardDTO(long totalUsuarios, long totalMascotas, long solicitudesPendientes) {
		this.totalUsuarios = totalUsuarios;
		this.totalMascotas = totalMascotas;
		this.solicitudesPendientes = solicitudesPendientes;
	}

	public long getTotalUsuarios() {
		return totalUsuarios;
	}

	public long getTotalMascotas() {
		return totalMascotas;
	}

	public long getSolicitudesPendientes() {
		return solicitudesPendientes;
	}
}
