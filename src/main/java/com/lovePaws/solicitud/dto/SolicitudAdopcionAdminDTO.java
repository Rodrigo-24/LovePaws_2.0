package com.lovePaws.solicitud.dto;

import java.time.LocalDateTime;

import com.lovePaws.solicitud.entity.EstadoSolicitud;

public class SolicitudAdopcionAdminDTO {

	private Long idSolicitud;
	private Long idMascota;
	private String nombreMascota;
	private String tipoMascota;
	private String nombresAdoptante;
	private String apellidosAdoptante;
	private LocalDateTime fechaSolicitud;
	private EstadoSolicitud estado;

	public SolicitudAdopcionAdminDTO(Long idSolicitud, Long idMascota, String nombreMascota, String tipoMascota,
			String nombresAdoptante, String apellidosAdoptante, LocalDateTime fechaSolicitud, EstadoSolicitud estado) {

		this.idSolicitud = idSolicitud;
		this.idMascota = idMascota;
		this.nombreMascota = nombreMascota;
		this.tipoMascota = tipoMascota;
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

	public String getTipoMascota() {
		return tipoMascota;
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