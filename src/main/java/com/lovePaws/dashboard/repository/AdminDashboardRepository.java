package com.lovePaws.dashboard.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AdminDashboardRepository {

	private final JdbcTemplate jdbcTemplate;

	public AdminDashboardRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	// Contar todos los usuarios
	public long contarUsuarios() {
		String sql = "SELECT COUNT(*) FROM usuario";
		return jdbcTemplate.queryForObject(sql, Long.class);
	}

	// Contar todas las mascotas
	public long contarMascotas() {
		String sql = "SELECT COUNT(*) FROM mascota";
		return jdbcTemplate.queryForObject(sql, Long.class);
	}

	// Contar solicitudes pendientes
	public long contarSolicitudesPendientes() {
		String sql = "SELECT COUNT(*) FROM solicitud_adopcion WHERE estado = 'PENDIENTE'";
		return jdbcTemplate.queryForObject(sql, Long.class);
	}
}
