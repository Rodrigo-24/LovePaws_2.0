package com.lovePaws.dashboard.dto;

public class AdminReporteDatoDTO {

	private String etiqueta;
	private long cantidad;

	public AdminReporteDatoDTO(String etiqueta, long cantidad) {
		this.etiqueta = etiqueta;
		this.cantidad = cantidad;
	}

	public String getEtiqueta() {
		return etiqueta;
	}

	public long getCantidad() {
		return cantidad;
	}
}