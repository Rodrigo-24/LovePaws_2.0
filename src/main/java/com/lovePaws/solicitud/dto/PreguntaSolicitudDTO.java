package com.lovePaws.solicitud.dto;

import com.lovePaws.solicitud.entity.TipoRespuestaPregunta;
import tools.jackson.databind.JsonNode;

public class PreguntaSolicitudDTO {

	private Long idPregunta;
	private String texto;
	private TipoRespuestaPregunta tipoRespuesta;
	private JsonNode opciones;
	private Short orden;
	private boolean obligatoria;

	public PreguntaSolicitudDTO(Long idPregunta, String texto, TipoRespuestaPregunta tipoRespuesta, JsonNode opciones,
			Short orden, boolean obligatoria) {
		this.idPregunta = idPregunta;
		this.texto = texto;
		this.tipoRespuesta = tipoRespuesta;
		this.opciones = opciones;
		this.orden = orden;
		this.obligatoria = obligatoria;
	}

	public Long getIdPregunta() {
		return idPregunta;
	}

	public String getTexto() {
		return texto;
	}

	public TipoRespuestaPregunta getTipoRespuesta() {
		return tipoRespuesta;
	}

	public JsonNode getOpciones() {
		return opciones;
	}

	public Short getOrden() {
		return orden;
	}

	public boolean isObligatoria() {
		return obligatoria;
	}
}