package com.lovePaws.dashboard.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.lovePaws.dashboard.dto.AdminReporteDatoDTO;

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

	// Contar todas las solicitudes
	public long contarSolicitudes() {
		String sql = "SELECT COUNT(*) FROM solicitud_adopcion";
		return jdbcTemplate.queryForObject(sql, Long.class);
	}

	// Contar solicitudes pendientes
	public long contarSolicitudesPendientes() {
		String sql = "SELECT COUNT(*) FROM solicitud_adopcion WHERE estado = 'PENDIENTE'";
		return jdbcTemplate.queryForObject(sql, Long.class);
	}

	// Contar solicitudes aprobadas
	public long contarSolicitudesAprobadas() {
		String sql = "SELECT COUNT(*) FROM solicitud_adopcion WHERE estado = 'APROBADA'";
		return jdbcTemplate.queryForObject(sql, Long.class);
	}

	// Contar solicitudes rechazadas
	public long contarSolicitudesRechazadas() {
		String sql = "SELECT COUNT(*) FROM solicitud_adopcion WHERE estado = 'RECHAZADA'";
		return jdbcTemplate.queryForObject(sql, Long.class);
	}

	// Contar mascotas disponibles y publicadas
	public long contarMascotasDisponibles() {
		String sql = """
				SELECT COUNT(*)
				FROM mascota
				WHERE estado_adopcion = 'DISPONIBLE'
				  AND estado_publicacion = 'ACTIVA'
				""";
		return jdbcTemplate.queryForObject(sql, Long.class);
	}

	// Contar mascotas adoptadas
	public long contarMascotasAdoptadas() {
		String sql = "SELECT COUNT(*) FROM mascota WHERE estado_adopcion = 'ADOPTADA'";
		return jdbcTemplate.queryForObject(sql, Long.class);
	}

	// Contar mascotas bloqueadas
	public long contarMascotasBloqueadas() {
		String sql = "SELECT COUNT(*) FROM mascota WHERE estado_publicacion = 'BLOQUEADA'";
		return jdbcTemplate.queryForObject(sql, Long.class);
	}

	// Obtener adopciones aprobadas por mes durante los últimos 12 meses
	public List<AdminReporteDatoDTO> obtenerAdopcionesPorMes() {

		String sql = """
				SELECT DATE_FORMAT(fecha_resolucion, '%Y-%m') AS etiqueta,
				       COUNT(*) AS cantidad
				FROM solicitud_adopcion
				WHERE estado = 'APROBADA'
				  AND fecha_resolucion >= DATE_SUB(
				      CAST(DATE_FORMAT(CURDATE(), '%Y-%m-01') AS DATE),
				      INTERVAL 11 MONTH
				  )
				GROUP BY DATE_FORMAT(fecha_resolucion, '%Y-%m')
				ORDER BY etiqueta
				""";

		return jdbcTemplate.query(sql, (rs, rowNum) -> new AdminReporteDatoDTO(rs.getString("etiqueta"), rs.getLong("cantidad")));
	}

	// Obtener la distribución de mascotas por tipo
	public List<AdminReporteDatoDTO> obtenerMascotasPorTipo() {

		String sql = """
				SELECT tm.nombre AS etiqueta,
				       COUNT(m.id_mascota) AS cantidad
				FROM tipo_mascota tm
				LEFT JOIN mascota m
				    ON m.id_tipo_mascota = tm.id_tipo_mascota
				GROUP BY tm.id_tipo_mascota, tm.nombre
				ORDER BY cantidad DESC, tm.nombre ASC
				""";

		return jdbcTemplate.query(sql, (rs, rowNum) -> new AdminReporteDatoDTO(rs.getString("etiqueta"), rs.getLong("cantidad")));
	}
}
