package com.lovePaws.mascota.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "tipo_mascota")
public class TipoMascota {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JdbcTypeCode(SqlTypes.SMALLINT)
	@Column(name = "id_tipo_mascota", nullable = false)
	private Integer idTipoMascota;

	@Column(name = "nombre", nullable = false, unique = true, length = 30)
	private String nombre;

	// Constructor requerido por JPA
	protected TipoMascota() {
	}

	// Constructor personalizado
	public TipoMascota(String nombre) {
		this.nombre = nombre;
	}

	
	public Integer getIdTipoMascota() {
		return idTipoMascota;
	}

	public void setIdTipoMascota(Integer idTipoMascota) {
		this.idTipoMascota = idTipoMascota;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
