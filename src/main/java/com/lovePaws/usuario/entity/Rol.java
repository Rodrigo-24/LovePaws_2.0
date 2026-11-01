package com.lovePaws.usuario.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rol")
public class Rol {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_rol", nullable = false, columnDefinition = "TINYINT UNSIGNED")
	private Byte idRol;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "nombre", nullable = false, unique = true, length = 20)
	private RolNombre nombre;
	
	@Column(name = "descripcion", length = 150)
	private String descripcion;
	
	protected Rol() {
		
	}
	
	public Rol(RolNombre nombre, String descripcion) {
		this.nombre = nombre;
		this.descripcion = descripcion;
	}

	public Byte getIdRol() {
		return idRol;
	}

	public void setIdRol(Byte idRol) {
		this.idRol = idRol;
	}

	public RolNombre getNombre() {
		return nombre;
	}

	public void setNombre(RolNombre nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
}
