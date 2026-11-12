package com.lovePaws.usuario.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginAdoptanteDTO {

	@NotBlank(message = "El nombre de usuario es obligatorio")
	private String username;

	@NotBlank(message = "La contraseña es obligatoria")
	private String password;

	public LoginAdoptanteDTO() {
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
