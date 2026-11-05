package com.lovePaws.solicitud.dto;

import com.lovePaws.solicitud.entity.EstadoSolicitud;

import java.time.LocalDateTime;

public class SolicitudAdopcionRespuestaDTO {

	private Long idSolicitud;
	private Long idMascota;
	private String nombreMascota;
	private String tipoMascota;
	private LocalDateTime fechaSolicitud;
	private EstadoSolicitud estado;

	public SolicitudAdopcionRespuestaDTO(Long idSolicitud, Long idMascota, String nombreMascota, String tipoMascota,
			LocalDateTime fechaSolicitud, EstadoSolicitud estado) {
		this.idSolicitud = idSolicitud;
		this.idMascota = idMascota;
		this.nombreMascota = nombreMascota;
		this.tipoMascota = tipoMascota;
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
	
	public String getTipoMascota() {
		return tipoMascota;
	}

	public LocalDateTime getFechaSolicitud() {
		return fechaSolicitud;
	}

	public EstadoSolicitud getEstado() {
		return estado;
	}
}