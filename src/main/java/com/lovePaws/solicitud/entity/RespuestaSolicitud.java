package com.lovePaws.solicitud.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "respuesta_solicitud")
public class RespuestaSolicitud {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_respuesta")
	private Long idRespuesta;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_solicitud", nullable = false)
	private SolicitudAdopcion solicitud;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pregunta")
	private PreguntaSolicitud pregunta;

	@Column(name = "pregunta_texto_snapshot", nullable = false, length = 500)
	private String preguntaTextoSnapshot;

	@Column(name = "respuesta", nullable = false, columnDefinition = "TEXT")
	private String respuesta;

	@Column(name = "orden_pregunta", nullable = false)
	private Short ordenPregunta;

	@CreationTimestamp
	@Column(name = "fecha_creacion", nullable = false, updatable = false)
	private LocalDateTime fechaCreacion;

	protected RespuestaSolicitud() {
	}

	public RespuestaSolicitud(SolicitudAdopcion solicitud, PreguntaSolicitud pregunta, String preguntaTextoSnapshot,
			String respuesta, Short ordenPregunta) {
		this.solicitud = solicitud;
		this.pregunta = pregunta;
		this.preguntaTextoSnapshot = preguntaTextoSnapshot;
		this.respuesta = respuesta;
		this.ordenPregunta = ordenPregunta;
	}

	
	public Long getIdRespuesta() {
		return idRespuesta;
	}

	public SolicitudAdopcion getSolicitud() {
		return solicitud;
	}

	public PreguntaSolicitud getPregunta() {
		return pregunta;
	}

	public String getPreguntaTextoSnapshot() {
		return preguntaTextoSnapshot;
	}

	public String getRespuesta() {
		return respuesta;
	}

	public Short getOrdenPregunta() {
		return ordenPregunta;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setIdRespuesta(Long idRespuesta) {
		this.idRespuesta = idRespuesta;
	}

	public void setSolicitud(SolicitudAdopcion solicitud) {
		this.solicitud = solicitud;
	}

	public void setPregunta(PreguntaSolicitud pregunta) {
		this.pregunta = pregunta;
	}

	public void setPreguntaTextoSnapshot(String preguntaTextoSnapshot) {
		this.preguntaTextoSnapshot = preguntaTextoSnapshot;
	}

	public void setRespuesta(String respuesta) {
		this.respuesta = respuesta;
	}

	public void setOrdenPregunta(Short ordenPregunta) {
		this.ordenPregunta = ordenPregunta;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
}