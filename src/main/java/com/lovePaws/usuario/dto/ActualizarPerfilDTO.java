package com.lovePaws.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ActualizarPerfilDTO {

	@NotBlank(message = "El nombre de usuario es obligatorio")
	@Size(min = 4, max = 40, message = "El usuario debe tener entre 4 y 40 caracteres")
	private String username;

	@NotBlank(message = "El correo electrónico es obligatorio")
	@Email(message = "El correo electrónico no es válido")
	@Size(max = 150)
	private String email;

	@NotBlank(message = "Debes ingresar tu contraseña actual")
	private String passwordActual;

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

	public String getPasswordActual() {
		return passwordActual;
	}

	public void setPasswordActual(String passwordActual) {
		this.passwordActual = passwordActual;
	}
}