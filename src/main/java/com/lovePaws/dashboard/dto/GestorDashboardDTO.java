package com.lovePaws.dashboard.dto;

public class GestorDashboardDTO {

	private long totalMascotas;
	private long mascotasDisponibles;
	private long solicitudesPendientes;

	public GestorDashboardDTO(long totalMascotas, long mascotasDisponibles, long solicitudesPendientes) {
		this.totalMascotas = totalMascotas;
		this.mascotasDisponibles = mascotasDisponibles;
		this.solicitudesPendientes = solicitudesPendientes;
	}

	public long getTotalMascotas() {
		return totalMascotas;
	}

	public long getMascotasDisponibles() {
		return mascotasDisponibles;
	}

	public long getSolicitudesPendientes() {
		return solicitudesPendientes;
	}
}