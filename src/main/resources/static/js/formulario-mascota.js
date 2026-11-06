
document.addEventListener("DOMContentLoaded", () => {

	// ==========================================
	// 1. Vista previa de la fotografía
	// ==========================================

	const inputFotografia = document.getElementById("fotografia");
	const previewImagen = document.getElementById("previewImagen");
	const placeholderImagen = document.getElementById("placeholderImagen");
	const errorFotografia = document.getElementById("errorFotografia");

	let urlAnterior = null;

	inputFotografia.addEventListener("change", () => {

		const archivo = inputFotografia.files[0];

		errorFotografia.textContent = "";

		if (!archivo) {
			return;
		}

		// Validar tamaño: 5 MB
		if (archivo.size > 5 * 1024 * 1024) {

			errorFotografia.textContent =
				"La fotografía no puede superar los 5 MB.";

			inputFotografia.value = "";

			return;
		}

		// Validar formato declarado para orientar al usuario
		const formatosPermitidos = [
			"image/jpeg",
			"image/png",
			"image/webp"
		];

		if (!formatosPermitidos.includes(archivo.type)) {

			errorFotografia.textContent =
				"Selecciona una imagen JPG, PNG o WEBP.";

			inputFotografia.value = "";

			return;
		}

		// Liberar la URL anterior
		if (urlAnterior) {
			URL.revokeObjectURL(urlAnterior);
		}

		// Crear URL temporal para mostrar la imagen
		urlAnterior = URL.createObjectURL(archivo);

		previewImagen.src = urlAnterior;
		previewImagen.hidden = false;
		placeholderImagen.hidden = true;

	});


	// ==========================================
	// 2. Filtrar razas según el tipo de mascota
	// ==========================================

	const selectTipo = document.getElementById("idTipoMascota");
	const selectRaza = document.getElementById("idRaza");

	function filtrarRazas() {

		const tipoSeleccionado = selectTipo.value;

		const opcionesRaza = selectRaza.querySelectorAll(
			"option[data-tipo]"
		);

		opcionesRaza.forEach(opcion => {

			const perteneceAlTipo =
				opcion.dataset.tipo === tipoSeleccionado;

			opcion.hidden = !perteneceAlTipo;
			opcion.disabled = !perteneceAlTipo;

		});

		// Limpiar la raza seleccionada si no corresponde al tipo
		const opcionSeleccionada =
			selectRaza.selectedOptions[0];

		if (
			opcionSeleccionada &&
			opcionSeleccionada.dataset.tipo !== tipoSeleccionado
		) {
			selectRaza.value = "";
		}
	}

	selectTipo.addEventListener("change", filtrarRazas);

	// Aplicar el filtro inicial
	filtrarRazas();

});
