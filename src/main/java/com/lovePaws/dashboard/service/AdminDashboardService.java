package com.lovePaws.dashboard.service;

import org.springframework.stereotype.Service;

import com.lovePaws.dashboard.dto.AdminDashboardDTO;
import com.lovePaws.dashboard.dto.AdminReportesResumenDTO;
import com.lovePaws.dashboard.repository.AdminDashboardRepository;
import com.lovePaws.dashboard.dto.AdminReporteDatoDTO;

import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

	public AdminReportesResumenDTO obtenerResumenReportes() {
		long totalSolicitudes = dashboardRepository.contarSolicitudes();
		long pendientes = dashboardRepository.contarSolicitudesPendientes();
		long aprobadas = dashboardRepository.contarSolicitudesAprobadas();
		long rechazadas = dashboardRepository.contarSolicitudesRechazadas();

		long disponibles = dashboardRepository.contarMascotasDisponibles();
		long adoptadas = dashboardRepository.contarMascotasAdoptadas();
		long bloqueadas = dashboardRepository.contarMascotasBloqueadas();

		// Calcular la tasa de aprobación sobre solicitudes resueltas
		long solicitudesResueltas = aprobadas + rechazadas;
		double tasaAprobacion = solicitudesResueltas == 0 ? 0.0 : (double) aprobadas / solicitudesResueltas * 100;
		return new AdminReportesResumenDTO(totalSolicitudes, pendientes, aprobadas, rechazadas, disponibles, adoptadas,
				bloqueadas, tasaAprobacion);
	}

	// Obtener adopciones aprobadas de los últimos 12 meses
	public List<AdminReporteDatoDTO> obtenerAdopcionesPorMes() {

		// Obtener los resultados de MySQL
		List<AdminReporteDatoDTO> datosBD = dashboardRepository.obtenerAdopcionesPorMes();

		// Organizar las cantidades por año y mes
		Map<String, Long> cantidadesPorMes = new HashMap<>();

		for (AdminReporteDatoDTO dato : datosBD) {
			cantidadesPorMes.put(dato.getEtiqueta(), dato.getCantidad());
		}

		// Construir los últimos 12 meses consecutivos
		List<AdminReporteDatoDTO> resultado = new ArrayList<>();

		YearMonth mesActual = YearMonth.now(ZoneId.of("America/Lima"));
		Locale locale = Locale.forLanguageTag("es-PE");

		DateTimeFormatter formato = DateTimeFormatter.ofPattern("MMM yyyy", locale);

		for (int i = 11; i >= 0; i--) {
			YearMonth mes = mesActual.minusMonths(i);
			String clave = mes.toString();
			String etiqueta = mes.format(formato);

			// Mostrar la primera letra del mes en mayúscula
			etiqueta = etiqueta.substring(0, 1).toUpperCase(locale) + etiqueta.substring(1);
			long cantidad = cantidadesPorMes.getOrDefault(clave, 0L);
			resultado.add(new AdminReporteDatoDTO(etiqueta, cantidad));
		}

		return resultado;
	}

	// Obtener la distribución de mascotas por tipo
	public List<AdminReporteDatoDTO> obtenerMascotasPorTipo() {
		return dashboardRepository.obtenerMascotasPorTipo();
	}
}