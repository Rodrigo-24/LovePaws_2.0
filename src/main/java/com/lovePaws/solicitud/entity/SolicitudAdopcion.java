package com.lovePaws.solicitud.entity;

import com.lovePaws.mascota.entity.Mascota;
import com.lovePaws.usuario.entity.Usuario;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitud_adopcion")
public class SolicitudAdopcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Long idSolicitud;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_mascota", nullable = false)
    private Mascota mascota;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_adoptante", nullable = false)
    private Usuario adoptante;

    @CreationTimestamp
    @Column(name = "fecha_solicitud", nullable = false, updatable = false)
    private LocalDateTime fechaSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 12)
    private EstadoSolicitud estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resuelto_por_usuario_id")
    private Usuario resueltoPor;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @Column(name = "observacion_resolucion", length = 1000)
    private String observacionResolucion;

    protected SolicitudAdopcion() {
    }

    public SolicitudAdopcion(Mascota mascota, Usuario adoptante) {
        this.mascota = mascota;
        this.adoptante = adoptante;
        this.estado = EstadoSolicitud.PENDIENTE;
    }

    
	public Long getIdSolicitud() {
		return idSolicitud;
	}

	public Mascota getMascota() {
		return mascota;
	}

	public Usuario getAdoptante() {
		return adoptante;
	}

	public LocalDateTime getFechaSolicitud() {
		return fechaSolicitud;
	}

	public EstadoSolicitud getEstado() {
		return estado;
	}

	public Usuario getResueltoPor() {
		return resueltoPor;
	}

	public LocalDateTime getFechaResolucion() {
		return fechaResolucion;
	}

	public String getObservacionResolucion() {
		return observacionResolucion;
	}

	public void setIdSolicitud(Long idSolicitud) {
		this.idSolicitud = idSolicitud;
	}

	public void setMascota(Mascota mascota) {
		this.mascota = mascota;
	}

	public void setAdoptante(Usuario adoptante) {
		this.adoptante = adoptante;
	}

	public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
		this.fechaSolicitud = fechaSolicitud;
	}

	public void setEstado(EstadoSolicitud estado) {
		this.estado = estado;
	}

	public void setResueltoPor(Usuario resueltoPor) {
		this.resueltoPor = resueltoPor;
	}

	public void setFechaResolucion(LocalDateTime fechaResolucion) {
		this.fechaResolucion = fechaResolucion;
	}

	public void setObservacionResolucion(String observacionResolucion) {
		this.observacionResolucion = observacionResolucion;
	}
}
