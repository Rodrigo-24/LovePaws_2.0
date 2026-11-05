
document.addEventListener("DOMContentLoaded", () => {

	const passwordInput = document.getElementById("password");
	const toggleButton = document.getElementById("togglePassword");

	const eyeIcon = document.getElementById("eyeIcon");
	const eyeSlashIcon = document.getElementById("eyeSlashIcon");

	if (!passwordInput || !toggleButton) {
		return;
	}

	toggleButton.addEventListener("click", () => {

		const mostrarPassword = passwordInput.type === "password";

		// Cambiar el tipo de campo
		passwordInput.type = mostrarPassword ? "text" : "password";

		// Alternar los iconos
		eyeIcon.hidden = mostrarPassword;
		eyeSlashIcon.hidden = !mostrarPassword;

		// Actualizar accesibilidad
		toggleButton.setAttribute(
			"aria-label",
			mostrarPassword ? "Ocultar contraseña" : "Mostrar contraseña"
		);

		toggleButton.setAttribute(
			"aria-pressed",
			String(mostrarPassword)
		);

		toggleButton.title = mostrarPassword
			? "Ocultar contraseña"
			: "Mostrar contraseña";

	});

});
