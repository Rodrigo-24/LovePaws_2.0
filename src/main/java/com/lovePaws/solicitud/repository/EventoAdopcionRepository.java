package com.lovePaws.solicitud.repository;

import com.lovePaws.solicitud.entity.EventoAdopcion;
import com.lovePaws.solicitud.entity.TipoEventoAdopcion;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoAdopcionRepository extends JpaRepository<EventoAdopcion, Long> {

	// Consultar el historial de una solicitud
	List<EventoAdopcion> findBySolicitud_IdSolicitudOrderByFechaEventoAsc(Long idSolicitud);

	// Consultar todos los eventos ordenados del más reciente al más antiguo
	Page<EventoAdopcion> findAllByOrderByFechaEventoDesc(Pageable pageable);

	// Consultar eventos por tipo, ordenados del más reciente al más antiguo
	Page<EventoAdopcion> findByTipoEventoOrderByFechaEventoDesc(TipoEventoAdopcion tipoEvento, Pageable pageable);
}