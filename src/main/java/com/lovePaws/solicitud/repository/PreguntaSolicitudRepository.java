package com.lovePaws.solicitud.repository;

import com.lovePaws.solicitud.entity.PreguntaSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreguntaSolicitudRepository extends JpaRepository<PreguntaSolicitud, Long> {

	// Obtener las preguntas activas ordenadas
	List<PreguntaSolicitud> findByActivaTrueOrderByOrdenAsc();

}