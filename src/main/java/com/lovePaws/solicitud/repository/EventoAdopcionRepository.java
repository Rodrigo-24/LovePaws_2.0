package com.lovePaws.solicitud.repository;

import com.lovePaws.solicitud.entity.EventoAdopcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoAdopcionRepository
        extends JpaRepository<EventoAdopcion, Long> {

    // Consultar el historial de una solicitud
    List<EventoAdopcion> findBySolicitud_IdSolicitudOrderByFechaEventoAsc(
            Long idSolicitud);
}