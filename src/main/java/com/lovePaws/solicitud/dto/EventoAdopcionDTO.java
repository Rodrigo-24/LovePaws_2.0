package com.lovePaws.solicitud.dto;

import com.lovePaws.solicitud.entity.TipoEventoAdopcion;

import java.time.LocalDateTime;

public class EventoAdopcionDTO {

	private Long idEvento;
	private TipoEventoAdopcion tipoEvento;
	private String detalle;
	private String nombreActor;
	private LocalDateTime fechaEvento;
	private Long idSolicitud;
	private String nombreMascota;
	private String nombresAdoptante;
	private String apellidosAdoptante;

	public EventoAdopcionDTO(Long idEvento, TipoEventoAdopcion tipoEvento, String detalle, String nombreActor,
			LocalDateTime fechaEvento) {
		this.idEvento = idEvento;
		this.tipoEvento = tipoEvento;
		this.detalle = detalle;
		this.nombreActor = nombreActor;
		this.fechaEvento = fechaEvento;
	}

	public EventoAdopcionDTO(Long idEvento, Long idSolicitud, String nombreMascota, String nombresAdoptante,
			String apellidosAdoptante, TipoEventoAdopcion tipoEvento, String detalle, String nombreActor,
			LocalDateTime fechaEvento) {

		this.idEvento = idEvento;
		this.idSolicitud = idSolicitud;
		this.nombreMascota = nombreMascota;
		this.nombresAdoptante = nombresAdoptante;
		this.apellidosAdoptante = apellidosAdoptante;
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

	public Long getIdSolicitud() {
		return idSolicitud;
	}

	public String getNombreMascota() {
		return nombreMascota;
	}

	public String getNombresAdoptante() {
		return nombresAdoptante;
	}

	public String getApellidosAdoptante() {
		return apellidosAdoptante;
	}
}