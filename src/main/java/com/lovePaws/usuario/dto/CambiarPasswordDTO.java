package com.lovePaws.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CambiarPasswordDTO {

	@NotBlank(message = "Debes ingresar tu contraseña actual")
	private String passwordActual;

	@NotBlank(message = "Debes ingresar una nueva contraseña")
	@Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
	private String passwordNueva;

	@NotBlank(message = "Debes confirmar tu nueva contraseña")
	private String confirmarPassword;

	public String getPasswordActual() {
		return passwordActual;
	}

	public void setPasswordActual(String passwordActual) {
		this.passwordActual = passwordActual;
	}

	public String getPasswordNueva() {
		return passwordNueva;
	}

	public void setPasswordNueva(String passwordNueva) {
		this.passwordNueva = passwordNueva;
	}

	public String getConfirmarPassword() {
		return confirmarPassword;
	}

	public void setConfirmarPassword(String confirmarPassword) {
		this.confirmarPassword = confirmarPassword;
	}
}