package com.lovePaws.solicitud.dto;

import jakarta.validation.constraints.Size;

public class ResolverSolicitudDTO {

	@Size(max = 1000, message = "La observación no puede superar los 1000 caracteres")
	private String observacion;

	public ResolverSolicitudDTO() {
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
}