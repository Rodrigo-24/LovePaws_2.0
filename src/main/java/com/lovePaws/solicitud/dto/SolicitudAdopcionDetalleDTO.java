package com.lovePaws.solicitud.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.lovePaws.solicitud.entity.EstadoSolicitud;

public class SolicitudAdopcionDetalleDTO {

	private Long idSolicitud;
	private Long idMascota;
	private String nombreMascota;
	private LocalDateTime fechaSolicitud;
	private EstadoSolicitud estado;

	private List<RespuestaSolicitudDetalleDTO> respuestas;

	public SolicitudAdopcionDetalleDTO(Long idSolicitud, Long idMascota, String nombreMascota,
			LocalDateTime fechaSolicitud, EstadoSolicitud estado, List<RespuestaSolicitudDetalleDTO> respuestas) {
		this.idSolicitud = idSolicitud;
		this.idMascota = idMascota;
		this.nombreMascota = nombreMascota;
		this.fechaSolicitud = fechaSolicitud;
		this.estado = estado;
		this.respuestas = respuestas;
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

	public List<RespuestaSolicitudDetalleDTO> getRespuestas() {
		return respuestas;
	}
}