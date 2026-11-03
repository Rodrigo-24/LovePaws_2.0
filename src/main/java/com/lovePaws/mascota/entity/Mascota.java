package com.lovePaws.mascota.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import com.lovePaws.usuario.entity.Usuario;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "mascota")
public class Mascota {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_mascota", nullable = false)
	private Long idMascota;

	// Relacion con TipoMascota
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_tipo_mascota", nullable = false)
	private TipoMascota tipoMascota;

	// Relacion con Raza
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_raza")
	private Raza raza;

	@Column(name = "nombre", nullable = false, length = 100)
	private String nombre;

	@JdbcTypeCode(SqlTypes.SMALLINT)
	@Column(name = "edad_anios")
	private Integer edadAnios;

	@JdbcTypeCode(SqlTypes.TINYINT)
	@Column(name = "edad_meses")
	private Short edadMeses;

	@Enumerated(EnumType.STRING)
	@Column(name = "sexo", length = 15)
	private SexoMascota sexo;

	@JdbcTypeCode(SqlTypes.LONGVARCHAR)
	@Column(name = "descripcion", columnDefinition = "TEXT")
	private String descripcion;

	@Column(name = "foto_url", length = 500)
	private String fotoUrl;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado_adopcion", nullable = false, length = 12)
	private EstadoAdopcion estadoAdopcion = EstadoAdopcion.DISPONIBLE;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado_publicacion", nullable = false, length = 12)
	private EstadoPublicacion estadoPublicacion = EstadoPublicacion.ACTIVA;

	// Usuario que registro la mascota
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "registrado_por_id", nullable = false)
	private Usuario registradoPor;

	// Usuario que actualizo la mascota
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "actualizado_por_id")
	private Usuario actualizadoPor;

	@CreationTimestamp
	@Column(name = "fecha_registro", nullable = false, updatable = false)
	private LocalDateTime fechaRegistro;

	@UpdateTimestamp
	@Column(name = "fecha_actualizacion", nullable = false)
	private LocalDateTime fechaActualizacion;

	// Constructor requerido por JPA
	protected Mascota() {
	}

	// Constructor personalizado
	public Mascota(TipoMascota tipoMascota, Raza raza, String nombre, Integer edadAnios, Short edadMeses,
			SexoMascota sexo, String descripcion, String fotoUrl, Usuario registradoPor) {
		this.tipoMascota = tipoMascota;
		this.raza = raza;
		this.nombre = nombre;
		this.edadAnios = edadAnios;
		this.edadMeses = edadMeses;
		this.sexo = sexo;
		this.descripcion = descripcion;
		this.fotoUrl = fotoUrl;
		this.registradoPor = registradoPor;
		this.estadoAdopcion = EstadoAdopcion.DISPONIBLE;
		this.estadoPublicacion = EstadoPublicacion.ACTIVA;
	}


	public Long getIdMascota() {
		return idMascota;
	}

	public TipoMascota getTipoMascota() {
		return tipoMascota;
	}

	public void setTipoMascota(TipoMascota tipoMascota) {
		this.tipoMascota = tipoMascota;
	}

	public Raza getRaza() {
		return raza;
	}

	public void setRaza(Raza raza) {
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

	public EstadoAdopcion getEstadoAdopcion() {
		return estadoAdopcion;
	}

	public void setEstadoAdopcion(EstadoAdopcion estadoAdopcion) {
		this.estadoAdopcion = estadoAdopcion;
	}

	public EstadoPublicacion getEstadoPublicacion() {
		return estadoPublicacion;
	}

	public void setEstadoPublicacion(EstadoPublicacion estadoPublicacion) {
		this.estadoPublicacion = estadoPublicacion;
	}

	public Usuario getRegistradoPor() {
		return registradoPor;
	}

	public Usuario getActualizadoPor() {
		return actualizadoPor;
	}

	public void setActualizadoPor(Usuario actualizadoPor) {
		this.actualizadoPor = actualizadoPor;
	}

	public LocalDateTime getFechaRegistro() {
		return fechaRegistro;
	}

	public LocalDateTime getFechaActualizacion() {
		return fechaActualizacion;
	}
}