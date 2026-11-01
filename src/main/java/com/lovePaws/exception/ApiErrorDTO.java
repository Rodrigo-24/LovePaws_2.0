package com.lovePaws.exception;

import java.time.LocalDateTime;

public class ApiErrorDTO {

	private LocalDateTime fecha;
    private int codigo;
    private String error;
    private String mensaje;
    private String ruta;
    
	public ApiErrorDTO(LocalDateTime fecha, int codigo, String error, String mensaje, String ruta) {
		this.fecha = fecha;
		this.codigo = codigo;
		this.error = error;
		this.mensaje = mensaje;
		this.ruta = ruta;
	}
    
	public LocalDateTime getFecha() {
        return fecha;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getError() {
        return error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getRuta() {
        return ruta;
    }
}
