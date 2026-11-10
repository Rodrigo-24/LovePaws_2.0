package com.lovePaws.solicitud.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.lovePaws.solicitud.entity.EstadoSolicitud;

public class SolicitudAdopcionDetalleDTO {

	private Long idSolicitud;
	private Long idMascota;

	private String nombreMascota;
	private String tipoMascota;
	private String fotoMascotaUrl;

	private String nombresAdoptante;
	private String apellidosAdoptante;
	private String correoAdoptante;

	private LocalDateTime fechaSolicitud;
	private EstadoSolicitud estado;

	private LocalDateTime fechaResolucion;
	private String nombreResponsable;
	private String observacionResolucion;

	private List<RespuestaSolicitudDetalleDTO> respuestas;

	public SolicitudAdopcionDetalleDTO(Long idSolicitud, Long idMascota, String nombreMascota, String tipoMascota,
			String fotoMascotaUrl, String nombresAdoptante, String apellidosAdoptante, String correoAdoptante,
			LocalDateTime fechaSolicitud, EstadoSolicitud estado, LocalDateTime fechaResolucion, 
			String nombreResponsable, String observacionResolucion, List<RespuestaSolicitudDetalleDTO> respuestas) {
		this.idSolicitud = idSolicitud;
		this.idMascota = idMascota;
		this.nombreMascota = nombreMascota;
		this.tipoMascota = tipoMascota;
		this.fotoMascotaUrl = fotoMascotaUrl;
		this.nombresAdoptante = nombresAdoptante;
		this.apellidosAdoptante = apellidosAdoptante;
		this.correoAdoptante = correoAdoptante;
		this.fechaSolicitud = fechaSolicitud;
		this.estado = estado;
		this.fechaResolucion = fechaResolucion;
		this.nombreResponsable = nombreResponsable;
		this.observacionResolucion = observacionResolucion;
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

	public String getTipoMascota() {
		return tipoMascota;
	}

	public String getFotoMascotaUrl() {
		return fotoMascotaUrl;
	}

	public String getNombresAdoptante() {
		return nombresAdoptante;
	}

	public String getApellidosAdoptante() {
		return apellidosAdoptante;
	}

	public String getCorreoAdoptante() {
		return correoAdoptante;
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

	public LocalDateTime getFechaResolucion() {
		return fechaResolucion;
	}

	public String getNombreResponsable() {
		return nombreResponsable;
	}

	public String getObservacionResolucion() {
		return observacionResolucion;
	}
}