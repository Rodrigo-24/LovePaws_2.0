package com.lovePaws.usuario.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    // Relacion con la tabla rol
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @Column(name = "username", nullable = false, unique = true, length = 40)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "nombres", nullable = false, length = 80)
    private String nombres;

    @Column(name = "apellidos", length = 80)
    private String apellidos;

    @Column(name = "foto_perfil_url", length = 500)
    private String fotoPerfilUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 12)
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;

    @Column(name = "fecha_bloqueo")
    private LocalDateTime fechaBloqueo;

    // Relacion con otro registro de la misma tabla usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bloqueado_por_id")
    private Usuario bloqueadoPor;

    @Column(name = "motivo_bloqueo", length = 500)
    private String motivoBloqueo;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;
    
    protected Usuario() {
    	
    }

	public Usuario(Rol rol, String username, String email, String passwordHash, String nombres, String apellidos,
			EstadoUsuario estado) {
		this.rol = rol;
		this.username = username;
		this.email = email;
		this.passwordHash = passwordHash;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.estado = estado;
	}

	public Long getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Rol getRol() {
		return rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getFotoPerfilUrl() {
		return fotoPerfilUrl;
	}

	public void setFotoPerfilUrl(String fotoPerfilUrl) {
		this.fotoPerfilUrl = fotoPerfilUrl;
	}

	public EstadoUsuario getEstado() {
		return estado;
	}

	public void setEstado(EstadoUsuario estado) {
		this.estado = estado;
	}

	public LocalDateTime getFechaBloqueo() {
		return fechaBloqueo;
	}

	public void setFechaBloqueo(LocalDateTime fechaBloqueo) {
		this.fechaBloqueo = fechaBloqueo;
	}

	public Usuario getBloqueadoPor() {
		return bloqueadoPor;
	}

	public void setBloqueadoPor(Usuario bloqueadoPor) {
		this.bloqueadoPor = bloqueadoPor;
	}

	public String getMotivoBloqueo() {
		return motivoBloqueo;
	}

	public void setMotivoBloqueo(String motivoBloqueo) {
		this.motivoBloqueo = motivoBloqueo;
	}
    
	public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}
