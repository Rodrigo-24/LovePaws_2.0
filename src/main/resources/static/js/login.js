document.addEventListener("DOMContentLoaded", () => {

	const botonesPassword = document.querySelectorAll(".password-toggle");

	botonesPassword.forEach(boton => {

		const input = document.getElementById(boton.dataset.target);

		const eyeIcon = boton.querySelector(".eye-icon");
		const eyeSlashIcon = boton.querySelector(".eye-slash-icon");

		if (!input) return;

		boton.addEventListener("click", () => {

			const mostrarPassword = input.type === "password";

			// Cambiar visibilidad
			input.type = mostrarPassword ? "text" : "password";

			// Cambiar iconos
			eyeIcon.hidden = mostrarPassword;
			eyeSlashIcon.hidden = !mostrarPassword;

			// Actualizar accesibilidad
			boton.setAttribute(
				"aria-label",
				mostrarPassword ? "Ocultar contraseña" : "Mostrar contraseña"
			);

			boton.setAttribute(
				"aria-pressed",
				String(mostrarPassword)
			);

			boton.title = mostrarPassword
				? "Ocultar contraseña"
				: "Mostrar contraseña";

		});

	});

});