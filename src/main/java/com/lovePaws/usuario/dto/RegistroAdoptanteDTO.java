package com.lovePaws.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistroAdoptanteDTO {
	
	@NotBlank(message = "El nombre de usuario es obligatorio")
	@Size(min = 4, max = 40)
	private String username;
	
	@NotBlank(message = "El correo electrónico es obligatorio")
	@Email(message = "El correo electrónico no es válido")
	@Size(max = 150)
	private String email;
	
	@NotBlank(message = "La contraseña es obligatoria")
	@Size(min = 8, max = 72)
	private String password;
	
	@NotBlank(message = "Los nombres son obligatorios")
	@Size(max = 80)
	private String nombres;
	
	@Size(max = 80)
	private String apellidos;
	
	public RegistroAdoptanteDTO() {
		
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
	
	
}
