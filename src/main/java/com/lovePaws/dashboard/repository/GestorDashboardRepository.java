package com.lovePaws.dashboard.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GestorDashboardRepository {

	private final JdbcTemplate jdbcTemplate;

	public GestorDashboardRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	// Contar las mascotas registradas por el gestor
	public long contarMascotas(Long idGestor) {

		String sql = "SELECT COUNT(*) FROM mascota WHERE registrado_por_id = ?";
		return jdbcTemplate.queryForObject(sql, Long.class, idGestor);
	}

	// Contar mascotas activas y disponibles
	public long contarMascotasDisponibles(Long idGestor) {

		String sql = """
				SELECT COUNT(*) FROM mascota WHERE registrado_por_id = ? AND estado_adopcion = 'DISPONIBLE'
				  AND estado_publicacion = 'ACTIVA'
				""";
		return jdbcTemplate.queryForObject(sql, Long.class, idGestor);
	}

	// Contar solicitudes pendientes de las mascotas del gestor
	public long contarSolicitudesPendientes(Long idGestor) {

		String sql = """
				SELECT COUNT(*) FROM solicitud_adopcion s
				INNER JOIN mascota m ON s.id_mascota = m.id_mascota
				WHERE m.registrado_por_id = ? AND s.estado = 'PENDIENTE'
				""";
		return jdbcTemplate.queryForObject(sql, Long.class, idGestor);
	}
}