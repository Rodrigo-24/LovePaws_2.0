package com.lovePaws.mascota.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;

@Entity
@Table(name = "raza")
public class Raza {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_raza", nullable = false)
	private Long idRaza;

	// Relacion con TipoMascota
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_tipo_mascota", nullable = false)
	private TipoMascota tipoMascota;

	@Column(name = "nombre", nullable = false, length = 80)
	private String nombre;

	@Column(name = "activa", nullable = false)
	private boolean activa = true;

	// Constructor requerido por JPA
	protected Raza() {
	}

	// Constructor personalizado
	public Raza(TipoMascota tipoMascota, String nombre) {
		this.tipoMascota = tipoMascota;
		this.nombre = nombre;
		this.activa = true;
	}


	public Long getIdRaza() {
		return idRaza;
	}

	public void setIdRaza(Long idRaza) {
		this.idRaza = idRaza;
	}

	public TipoMascota getTipoMascota() {
		return tipoMascota;
	}

	public void setTipoMascota(TipoMascota tipoMascota) {
		this.tipoMascota = tipoMascota;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public boolean isActiva() {
		return activa;
	}

	public void setActiva(boolean activa) {
		this.activa = activa;
	}
}