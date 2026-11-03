package com.lovePaws.solicitud.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List; 

public class CrearSolicitudDTO {

	@NotNull(message = "El ID de la mascota es obligatorio")
	@Positive(message = "El ID de la mascota debe ser positivo")
	private Long idMascota;

	@NotEmpty(message = "Debes enviar las respuestas del formulario")
	@Valid
	private List<RespuestaSolicitudDTO> respuestas;

	public CrearSolicitudDTO() {
	}

	public Long getIdMascota() {
		return idMascota;
	}

	public void setIdMascota(Long idMascota) {
		this.idMascota = idMascota;
	}

	public List<RespuestaSolicitudDTO> getRespuestas() {
		return respuestas;
	}

	public void setRespuestas(List<RespuestaSolicitudDTO> respuestas) {
		this.respuestas = respuestas;
	}
}
