package com.lovePaws.solicitud.dto;

import com.lovePaws.solicitud.entity.TipoEventoAdopcion;

import java.time.LocalDateTime;

public class EventoAdopcionDTO {

	private Long idEvento;
	private TipoEventoAdopcion tipoEvento;
	private String detalle;
	private String nombreActor;
	private LocalDateTime fechaEvento;

	public EventoAdopcionDTO(Long idEvento, TipoEventoAdopcion tipoEvento, String detalle, String nombreActor, LocalDateTime fechaEvento) {
		this.idEvento = idEvento;
		this.tipoEvento = tipoEvento;
		this.detalle = detalle;
		this.nombreActor = nombreActor;
		this.fechaEvento = fechaEvento;
	}

	public Long getIdEvento() {
		return idEvento;
	}

	public TipoEventoAdopcion getTipoEvento() {
		return tipoEvento;
	}

	public String getDetalle() {
		return detalle;
	}

	public String getNombreActor() {
		return nombreActor;
	}

	public LocalDateTime getFechaEvento() {
		return fechaEvento;
	}
}