package com.lovePaws.solicitud.entity;

import tools.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "pregunta_solicitud")
public class PreguntaSolicitud {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_pregunta")
	private Long idPregunta;

	@Column(name = "texto", nullable = false, length = 500)
	private String texto;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_respuesta", nullable = false, length = 15)
	private TipoRespuestaPregunta tipoRespuesta;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "opciones_json")
	private JsonNode opciones;

	@Column(name = "orden", nullable = false)
	private Short orden;

	@Column(name = "obligatoria", nullable = false)
	private boolean obligatoria;

	@Column(name = "activa", nullable = false)
	private boolean activa;

	@CreationTimestamp
	@Column(name = "fecha_creacion", nullable = false, updatable = false)
	private LocalDateTime fechaCreacion;

	@UpdateTimestamp
	@Column(name = "fecha_actualizacion", nullable = false)
	private LocalDateTime fechaActualizacion;

	protected PreguntaSolicitud() {
	}

	public PreguntaSolicitud(String texto, TipoRespuestaPregunta tipoRespuesta, JsonNode opciones, Short orden,
			boolean obligatoria) {
		this.texto = texto;
		this.tipoRespuesta = tipoRespuesta;
		this.opciones = opciones;
		this.orden = orden;
		this.obligatoria = obligatoria;
		this.activa = true;
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

	public boolean isActiva() {
		return activa;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public LocalDateTime getFechaActualizacion() {
		return fechaActualizacion;
	}

	public void setIdPregunta(Long idPregunta) {
		this.idPregunta = idPregunta;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public void setTipoRespuesta(TipoRespuestaPregunta tipoRespuesta) {
		this.tipoRespuesta = tipoRespuesta;
	}

	public void setOpciones(JsonNode opciones) {
		this.opciones = opciones;
	}

	public void setOrden(Short orden) {
		this.orden = orden;
	}

	public void setObligatoria(boolean obligatoria) {
		this.obligatoria = obligatoria;
	}

	public void setActiva(boolean activa) {
		this.activa = activa;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}
}