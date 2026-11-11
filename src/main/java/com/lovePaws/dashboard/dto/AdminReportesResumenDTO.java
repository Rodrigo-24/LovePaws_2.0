package com.lovePaws.dashboard.dto;

public class AdminReportesResumenDTO {

	private long totalSolicitudes;
	private long solicitudesPendientes;
	private long solicitudesAprobadas;
	private long solicitudesRechazadas;

	private long mascotasDisponibles;
	private long mascotasAdoptadas;
	private long mascotasBloqueadas;

	private double tasaAprobacion;

	public AdminReportesResumenDTO(long totalSolicitudes, long solicitudesPendientes, long solicitudesAprobadas,
			long solicitudesRechazadas, long mascotasDisponibles, long mascotasAdoptadas, long mascotasBloqueadas,
			double tasaAprobacion) {

		this.totalSolicitudes = totalSolicitudes;
		this.solicitudesPendientes = solicitudesPendientes;
		this.solicitudesAprobadas = solicitudesAprobadas;
		this.solicitudesRechazadas = solicitudesRechazadas;
		this.mascotasDisponibles = mascotasDisponibles;
		this.mascotasAdoptadas = mascotasAdoptadas;
		this.mascotasBloqueadas = mascotasBloqueadas;
		this.tasaAprobacion = tasaAprobacion;
	}

	public long getTotalSolicitudes() {
		return totalSolicitudes;
	}

	public long getSolicitudesPendientes() {
		return solicitudesPendientes;
	}

	public long getSolicitudesAprobadas() {
		return solicitudesAprobadas;
	}

	public long getSolicitudesRechazadas() {
		return solicitudesRechazadas;
	}

	public long getMascotasDisponibles() {
		return mascotasDisponibles;
	}

	public long getMascotasAdoptadas() {
		return mascotasAdoptadas;
	}

	public long getMascotasBloqueadas() {
		return mascotasBloqueadas;
	}

	public double getTasaAprobacion() {
		return tasaAprobacion;
	}

}
