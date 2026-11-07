package com.lovePaws.solicitud.dto;

import jakarta.validation.constraints.NotNull;

public class RespuestaSolicitudDTO {

	@NotNull(message = "El ID de la pregunta es obligatorio")
	private Long idPregunta;

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