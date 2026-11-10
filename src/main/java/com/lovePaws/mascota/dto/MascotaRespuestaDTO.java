package com.lovePaws.mascota.dto;

import java.time.LocalDateTime;

import com.lovePaws.mascota.entity.EstadoAdopcion;
import com.lovePaws.mascota.entity.EstadoPublicacion;
import com.lovePaws.mascota.entity.SexoMascota;

public class MascotaRespuestaDTO {

	private Long idMascota;
	private String nombre;
	private String tipoMascota;
	private String raza;
	private Integer edadAnios;
	private Short edadMeses;
	private SexoMascota sexo;
	private String descripcion;
	private String fotoUrl;
	private EstadoAdopcion estadoAdopcion;
	private EstadoPublicacion estadoPublicacion;
	private LocalDateTime fechaRegistro;
	private String usernameGestor;
	private String nombreGestor;

	public MascotaRespuestaDTO(Long idMascota, String nombre, String tipoMascota, String raza, Integer edadAnios,
			Short edadMeses, SexoMascota sexo, String descripcion, String fotoUrl, EstadoAdopcion estadoAdopcion,
			EstadoPublicacion estadoPublicacion, LocalDateTime fechaRegistro, String usernameGestor, String nombreGestor) {
		this.idMascota = idMascota;
		this.nombre = nombre;
		this.tipoMascota = tipoMascota;
		this.raza = raza;
		this.edadAnios = edadAnios;
		this.edadMeses = edadMeses;
		this.sexo = sexo;
		this.descripcion = descripcion;
		this.fotoUrl = fotoUrl;
		this.estadoAdopcion = estadoAdopcion;
		this.estadoPublicacion = estadoPublicacion;
		this.fechaRegistro = fechaRegistro;
		this.usernameGestor = usernameGestor;
		this.nombreGestor = nombreGestor;
	}


	public Long getIdMascota() {
		return idMascota;
	}

	public String getNombre() {
		return nombre;
	}

	public String getTipoMascota() {
		return tipoMascota;
	}

	public String getRaza() {
		return raza;
	}

	public Integer getEdadAnios() {
		return edadAnios;
	}

	public Short getEdadMeses() {
		return edadMeses;
	}

	public SexoMascota getSexo() {
		return sexo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public String getFotoUrl() {
		return fotoUrl;
	}

	public EstadoAdopcion getEstadoAdopcion() {
		return estadoAdopcion;
	}

	public EstadoPublicacion getEstadoPublicacion() {
		return estadoPublicacion;
	}

	public LocalDateTime getFechaRegistro() {
		return fechaRegistro;
	}
	
	public String getUsernameGestor() {
	    return usernameGestor;
	}

	public String getNombreGestor() {
	    return nombreGestor;
	}
}