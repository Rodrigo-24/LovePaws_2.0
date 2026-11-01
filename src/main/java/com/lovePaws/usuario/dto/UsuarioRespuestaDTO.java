package com.lovePaws.usuario.dto;

import java.time.LocalDateTime;

import com.lovePaws.usuario.entity.EstadoUsuario;
import com.lovePaws.usuario.entity.RolNombre;

public class UsuarioRespuestaDTO {

	private Long idUsuario;
    private String username;
    private String email;
    private String nombres;
    private String apellidos;
    private String fotoPerfilUrl;
    private RolNombre rol;
    private EstadoUsuario estado;
    private LocalDateTime fechaCreacion;
    
    public UsuarioRespuestaDTO() {
    	
    }

	public UsuarioRespuestaDTO(Long idUsuario, String username, String email, String nombres, String apellidos,
			String fotoPerfilUrl, RolNombre rol, EstadoUsuario estado, LocalDateTime fechaCreacion) {
		this.idUsuario = idUsuario;
		this.username = username;
		this.email = email;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.fotoPerfilUrl = fotoPerfilUrl;
		this.rol = rol;
		this.estado = estado;
		this.fechaCreacion = fechaCreacion;
	}

	public Long getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
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

	public RolNombre getRol() {
		return rol;
	}

	public void setRol(RolNombre rol) {
		this.rol = rol;
	}

	public EstadoUsuario getEstado() {
		return estado;
	}

	public void setEstado(EstadoUsuario estado) {
		this.estado = estado;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
    
	
    
}
