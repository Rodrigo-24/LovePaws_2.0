package com.lovePaws.solicitud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RespuestaSolicitudDTO {

	@NotNull(message = "El ID de la pregunta es obligatorio")
	private Long idPregunta;

	@NotBlank(message = "La respuesta no puede estar vacía")
	private String respuesta;

	public RespuestaSolicitudDTO() {
	}

	public Long getIdPregunta() {
		return idPregunta;
	}

	public void setIdPregunta(Long idPregunta) {
		this.idPregunta = idPregunta;
	}

	public String getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(String respuesta) {
		this.respuesta = respuesta;
	}
}