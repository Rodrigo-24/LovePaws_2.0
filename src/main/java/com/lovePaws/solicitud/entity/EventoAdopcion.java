package com.lovePaws.solicitud.entity;

import com.lovePaws.usuario.entity.Usuario;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "evento_adopcion")
public class EventoAdopcion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_evento")
	private Long idEvento;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_solicitud", nullable = false)
	private SolicitudAdopcion solicitud;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "actor_usuario_id")
	private Usuario actor;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_evento", nullable = false, length = 45)
	private TipoEventoAdopcion tipoEvento;

	@Column(name = "detalle", length = 1000)
	private String detalle;

	@CreationTimestamp
	@Column(name = "fecha_evento", nullable = false, updatable = false)
	private LocalDateTime fechaEvento;

	protected EventoAdopcion() {
	}

	public EventoAdopcion(SolicitudAdopcion solicitud, Usuario actor, TipoEventoAdopcion tipoEvento, String detalle) {
		this.solicitud = solicitud;
		this.actor = actor;
		this.tipoEvento = tipoEvento;
		this.detalle = detalle;
	}

	
	public Long getIdEvento() {
		return idEvento;
	}

	public SolicitudAdopcion getSolicitud() {
		return solicitud;
	}

	public Usuario getActor() {
		return actor;
	}

	public TipoEventoAdopcion getTipoEvento() {
		return tipoEvento;
	}

	public String getDetalle() {
		return detalle;
	}

	public LocalDateTime getFechaEvento() {
		return fechaEvento;
	}

	public void setIdEvento(Long idEvento) {
		this.idEvento = idEvento;
	}

	public void setSolicitud(SolicitudAdopcion solicitud) {
		this.solicitud = solicitud;
	}

	public void setActor(Usuario actor) {
		this.actor = actor;
	}

	public void setTipoEvento(TipoEventoAdopcion tipoEvento) {
		this.tipoEvento = tipoEvento;
	}

	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}

	public void setFechaEvento(LocalDateTime fechaEvento) {
		this.fechaEvento = fechaEvento;
	}
}