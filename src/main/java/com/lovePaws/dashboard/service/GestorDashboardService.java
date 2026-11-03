package com.lovePaws.dashboard.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lovePaws.dashboard.dto.GestorDashboardDTO;
import com.lovePaws.dashboard.repository.GestorDashboardRepository;
import com.lovePaws.usuario.entity.Usuario;
import com.lovePaws.usuario.repository.UsuarioRepository;

@Service
public class GestorDashboardService {

	private final GestorDashboardRepository dashboardRepository;
	private final UsuarioRepository usuarioRepository;

	public GestorDashboardService(GestorDashboardRepository dashboardRepository, UsuarioRepository usuarioRepository) {
		this.dashboardRepository = dashboardRepository;
		this.usuarioRepository = usuarioRepository;
	}

	@Transactional(readOnly = true)
	public GestorDashboardDTO obtenerResumen(String username) {

		// 1. Buscar el usuario autenticado
		Usuario gestor = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new IllegalStateException("No se encontró el usuario autenticado."));

		// 2. Obtener su identificador
		Long idGestor = gestor.getIdUsuario();

		// 3. Consultar las estadísticas
		long totalMascotas = dashboardRepository.contarMascotas(idGestor);
		long mascotasDisponibles = dashboardRepository.contarMascotasDisponibles(idGestor);
		long solicitudesPendientes = dashboardRepository.contarSolicitudesPendientes(idGestor);

		// 4. Construir el DTO
		return new GestorDashboardDTO(totalMascotas, mascotasDisponibles, solicitudesPendientes);
	}
}
