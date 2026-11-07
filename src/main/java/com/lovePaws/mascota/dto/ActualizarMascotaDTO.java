package com.lovePaws.mascota.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

import com.lovePaws.mascota.entity.SexoMascota;

public class ActualizarMascotaDTO {

	@NotNull(message = "Debe seleccionar un tipo de mascota")
	@Positive(message = "El tipo de mascota no es válido")
	private Integer idTipoMascota;

	@Positive(message = "La raza seleccionada no es válida")
	private Long idRaza;
	
	@Size(max = 80, message = "La raza no puede superar los 80 caracteres")
	private String raza;

	@NotBlank(message = "El nombre de la mascota es obligatorio")
	@Size(max = 100)
	private String nombre;

	@Min(value = 0, message = "La edad no puede ser negativa")
	@Max(value = 65535, message = "La edad supera el límite permitido")
	private Integer edadAnios;

	@Min(value = 0, message = "Los meses no pueden ser negativos")
	@Max(value = 11, message = "Los meses deben estar entre 0 y 11")
	private Short edadMeses;

	private SexoMascota sexo;

	private String descripcion;

	@Size(max = 500)
	private String fotoUrl;

	public ActualizarMascotaDTO() {
	}


	public Integer getIdTipoMascota() {
		return idTipoMascota;
	}

	public void setIdTipoMascota(Integer idTipoMascota) {
		this.idTipoMascota = idTipoMascota;
	}

	public Long getIdRaza() {
		return idRaza;
	}

	public void setIdRaza(Long idRaza) {
		this.idRaza = idRaza;
	}
	
	public String getRaza() {
	    return raza;
	}

	public void setRaza(String raza) {
	    this.raza = raza;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Integer getEdadAnios() {
		return edadAnios;
	}

	public void setEdadAnios(Integer edadAnios) {
		this.edadAnios = edadAnios;
	}

	public Short getEdadMeses() {
		return edadMeses;
	}

	public void setEdadMeses(Short edadMeses) {
		this.edadMeses = edadMeses;
	}

	public SexoMascota getSexo() {
		return sexo;
	}

	public void setSexo(SexoMascota sexo) {
		this.sexo = sexo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getFotoUrl() {
		return fotoUrl;
	}

	public void setFotoUrl(String fotoUrl) {
		this.fotoUrl = fotoUrl;
	}
}
