package com.lovePaws.solicitud.dto;

public class RespuestaSolicitudDetalleDTO {

	private String pregunta;
	private String respuesta;
	private Short ordenPregunta;

	public RespuestaSolicitudDetalleDTO(String pregunta, String respuesta, Short ordenPregunta) {
		this.pregunta = pregunta;
		this.respuesta = respuesta;
		this.ordenPregunta = ordenPregunta;
	}

	public String getPregunta() {
		return pregunta;
	}

	public String getRespuesta() {
		return respuesta;
	}

	public Short getOrdenPregunta() {
		return ordenPregunta;
	}
}