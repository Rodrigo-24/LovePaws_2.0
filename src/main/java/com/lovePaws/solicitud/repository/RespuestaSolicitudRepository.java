package com.lovePaws.solicitud.repository;

import com.lovePaws.solicitud.entity.RespuestaSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RespuestaSolicitudRepository extends JpaRepository<RespuestaSolicitud, Long> {

	// Obtener las respuestas de una solicitud, ordenadas según el formulario
	List<RespuestaSolicitud> findBySolicitud_IdSolicitudOrderByOrdenPreguntaAsc(Long idSolicitud);

}