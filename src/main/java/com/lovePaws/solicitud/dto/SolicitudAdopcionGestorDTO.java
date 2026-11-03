package com.lovePaws.solicitud.dto;

import java.time.LocalDateTime;

import com.lovePaws.solicitud.entity.EstadoSolicitud;

public class SolicitudAdopcionGestorDTO {

	private Long idSolicitud;
	private Long idMascota;
	private String nombreMascota;
	private String nombresAdoptante;
	private String apellidosAdoptante;
	private LocalDateTime fechaSolicitud;
	private EstadoSolicitud estado;

	public SolicitudAdopcionGestorDTO(Long idSolicitud, Long idMascota, String nombreMascota, String nombresAdoptante,
			String apellidosAdoptante, LocalDateTime fechaSolicitud, EstadoSolicitud estado) {
		this.idSolicitud = idSolicitud;
		this.idMascota = idMascota;
		this.nombreMascota = nombreMascota;
		this.nombresAdoptante = nombresAdoptante;
		this.apellidosAdoptante = apellidosAdoptante;
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

	public String getNombresAdoptante() {
		return nombresAdoptante;
	}

	public String getApellidosAdoptante() {
		return apellidosAdoptante;
	}

	public LocalDateTime getFechaSolicitud() {
		return fechaSolicitud;
	}

	public EstadoSolicitud getEstado() {
		return estado;
	}
}