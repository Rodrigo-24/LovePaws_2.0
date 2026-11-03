package com.lovePaws.solicitud.repository;

import com.lovePaws.solicitud.entity.SolicitudAdopcion;
import com.lovePaws.solicitud.entity.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

public interface SolicitudAdopcionRepository extends JpaRepository<SolicitudAdopcion, Long> {

	// Consultar solicitudes de un adoptante
	List<SolicitudAdopcion> findByAdoptante_IdUsuarioOrderByFechaSolicitudDesc(Long idAdoptante);

	// Consultar solicitudes de las mascotas registradas por un gestor
	List<SolicitudAdopcion> findByMascota_RegistradoPor_IdUsuarioAndEstadoOrderByFechaSolicitudDesc(Long idGestor,
			EstadoSolicitud estado);

	// Comprobar si ya existe una solicitud con ese estado
	boolean existsByMascota_IdMascotaAndAdoptante_IdUsuarioAndEstado(Long idMascota, Long idAdoptante,
			EstadoSolicitud estado);

	// Buscar una solicitud y bloquear su registro
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT s FROM SolicitudAdopcion s WHERE s.idSolicitud = :idSolicitud")
	Optional<SolicitudAdopcion> buscarPorIdConBloqueo(@Param("idSolicitud") Long idSolicitud);

	// Obtener y bloquear las solicitudes pendientes de una mascota
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT s FROM SolicitudAdopcion s WHERE s.mascota.idMascota = :idMascota AND s.estado = :estado")
	List<SolicitudAdopcion> buscarPorMascotaYEstadoConBloqueo(@Param("idMascota") Long idMascota, @Param("estado") EstadoSolicitud estado);
}
