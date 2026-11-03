package com.lovePaws.mascota.repository;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovePaws.mascota.entity.Mascota;
import com.lovePaws.mascota.entity.EstadoAdopcion;
import com.lovePaws.mascota.entity.EstadoPublicacion;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {

	// Consultar mascotas registradas por un gestor
	List<Mascota> findByRegistradoPor_IdUsuarioOrderByFechaRegistroDesc(Long idUsuario);

	// Buscar una mascota perteneciente a un gestor
	Optional<Mascota> findByIdMascotaAndRegistradoPor_IdUsuario(Long idMascota, Long idUsuario);

	// Consultar mascotas disponibles y publicadas
	List<Mascota> findByEstadoPublicacionAndEstadoAdopcionOrderByFechaRegistroDesc(EstadoPublicacion estadoPublicacion,
			EstadoAdopcion estadoAdopcion);

	// Filtrar mascotas por tipo
	List<Mascota> findByTipoMascota_IdTipoMascotaAndEstadoPublicacionAndEstadoAdopcion(Integer idTipoMascota,
			EstadoPublicacion estadoPublicacion, EstadoAdopcion estadoAdopcion);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT m FROM Mascota m WHERE m.idMascota = :idMascota")
	Optional<Mascota> buscarPorIdConBloqueo(@Param("idMascota") Long idMascota);
}
