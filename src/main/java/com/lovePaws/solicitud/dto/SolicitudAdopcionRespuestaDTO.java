package com.lovePaws.solicitud.dto;

import com.lovePaws.solicitud.entity.EstadoSolicitud;

import java.time.LocalDateTime;

public class SolicitudAdopcionRespuestaDTO {

	private Long idSolicitud;
	private Long idMascota;
	private String nombreMascota;
	private LocalDateTime fechaSolicitud;
	private EstadoSolicitud estado;

	public SolicitudAdopcionRespuestaDTO(Long idSolicitud, Long idMascota, String nombreMascota,
			LocalDateTime fechaSolicitud, EstadoSolicitud estado) {
		this.idSolicitud = idSolicitud;
		this.idMascota = idMascota;
		this.nombreMascota = nombreMascota;
		this.fechaSolicitud = fechaSolicitud;
		this.estado = estado;
	}

	public Long getIdSolicitud() {
		return idSolicitud;
	}

	public Long getIdMascota() {
		return idMascota;
	}

	public String getNombreMascota() {
		return nombreMascota;
	}

	public LocalDateTime getFechaSolicitud() {
		return fechaSolicitud;
	}

	public EstadoSolicitud getEstado() {
		return estado;
	}
}