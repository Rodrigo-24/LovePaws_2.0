
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
	// 2. Autocompletado de razas
	// ==========================================

	const selectTipo = document.getElementById("idTipoMascota");
	const inputRaza = document.getElementById("raza");

	const catalogoRazas = document.getElementById("catalogoRazas");
	const sugerenciasRaza = document.getElementById("sugerenciasRaza");
	const opcionesRaza = document.getElementById("opcionesRaza");
	const mensajeSugerencias = document.getElementById("mensajeSugerencias");

	// Obtener las razas cargadas desde Thymeleaf
	const razasDisponibles = Array.from(
		catalogoRazas.querySelectorAll("[data-nombre]")
	).map(elemento => ({
		nombre: elemento.dataset.nombre,
		tipo: elemento.dataset.tipo
	}));

	let opcionesActuales = [];
	let indiceActivo = -1;

	// Normalizar texto para comparar nombres
	function normalizarTexto(texto) {
		return texto
			.trim()
			.toLocaleLowerCase("es")
			.normalize("NFD")
			.replace(/[\u0300-\u036f]/g, "")
			.replace(/\s+/g, " ");
	}

	// Cerrar sugerencias
	function ocultarSugerencias() {
		sugerenciasRaza.hidden = true;
		inputRaza.setAttribute("aria-expanded", "false");
		inputRaza.removeAttribute("aria-activedescendant");

		indiceActivo = -1;
	}

	// Actualizar opción activa con el teclado
	function actualizarOpcionActiva() {

		const botones = opcionesRaza.querySelectorAll(
			".breed-suggestion-option"
		);

		botones.forEach((boton, indice) => {

			const activo = indice === indiceActivo;

			boton.setAttribute("aria-selected", activo);

			if (activo) {
				inputRaza.setAttribute(
					"aria-activedescendant",
					boton.id
				);

				boton.scrollIntoView({
					block: "nearest"
				});
			}
		});

		if (indiceActivo === -1) {
			inputRaza.removeAttribute("aria-activedescendant");
		}
	}

	// Mostrar las sugerencias
	function mostrarSugerencias() {

		const tipoSeleccionado = selectTipo.value;
		const termino = normalizarTexto(inputRaza.value);

		opcionesRaza.replaceChildren();

		indiceActivo = -1;
		opcionesActuales = [];

		if (!tipoSeleccionado) {
			ocultarSugerencias();
			return;
		}

		// Filtrar por tipo y texto escrito
		const resultados = razasDisponibles
			.filter(raza => {

				const mismoTipo = raza.tipo === tipoSeleccionado;

				const coincide = normalizarTexto(raza.nombre)
					.includes(termino);

				return mismoTipo && coincide;

			})
			.sort((a, b) =>
				a.nombre.localeCompare(b.nombre, "es")
			);

		// Mostrar como máximo 8 sugerencias
		opcionesActuales = resultados.slice(0, 8);

		opcionesActuales.forEach((raza, indice) => {

			const boton = document.createElement("button");

			boton.type = "button";
			boton.id = `sugerencia-raza-${indice}`;
			boton.className = "breed-suggestion-option";
			boton.setAttribute("role", "option");
			boton.setAttribute("aria-selected", "false");

			const nombre = document.createElement("span");
			nombre.textContent = raza.nombre;

			const etiqueta = document.createElement("span");
			etiqueta.className = "breed-suggestion-tag";
			etiqueta.textContent = "Registrada";

			boton.append(nombre, etiqueta);

			// Evitar que se pierda el foco antes de seleccionar
			boton.addEventListener("pointerdown", evento => {
				evento.preventDefault();
			});

			// Seleccionar raza
			boton.addEventListener("click", () => {

				inputRaza.value = raza.nombre;

				ocultarSugerencias();

				inputRaza.focus();

			});

			opcionesRaza.appendChild(boton);

		});

		// Mensajes informativos
		if (resultados.length === 0) {

			const mensaje = document.createElement("div");

			mensaje.className = "breed-suggestion-empty";

			mensaje.textContent = termino
				? "No encontramos coincidencias. Esta raza se registrará como nueva al guardar."
				: "Todavía no hay razas registradas para este tipo.";

			opcionesRaza.appendChild(mensaje);

		} else if (resultados.length > 8) {

			mensajeSugerencias.textContent =
				"Mostrando las primeras 8 coincidencias.";

		}

		if (resultados.length <= 8) {
			mensajeSugerencias.textContent =
				"Selecciona una sugerencia o continúa escribiendo una raza nueva.";
		}

		sugerenciasRaza.hidden = false;

		inputRaza.setAttribute("aria-expanded", "true");
	}

	// Mostrar sugerencias al escribir
	inputRaza.addEventListener("input", mostrarSugerencias);

	// Mostrar sugerencias al seleccionar el campo
	inputRaza.addEventListener("focus", mostrarSugerencias);

	// Limpiar la raza al cambiar el tipo
	selectTipo.addEventListener("change", () => {

		inputRaza.value = "";

		ocultarSugerencias();

	});

	// Navegación mediante teclado
	inputRaza.addEventListener("keydown", evento => {

		if (evento.key === "ArrowDown") {

			if (sugerenciasRaza.hidden) {
				mostrarSugerencias();
			}

			if (opcionesActuales.length === 0) return;

			evento.preventDefault();

			indiceActivo = Math.min(
				indiceActivo + 1,
				opcionesActuales.length - 1
			);

			actualizarOpcionActiva();

		} else if (evento.key === "ArrowUp") {

			if (opcionesActuales.length === 0) return;

			evento.preventDefault();

			indiceActivo = Math.max(
				indiceActivo - 1,
				0
			);

			actualizarOpcionActiva();

		} else if (evento.key === "Enter" && indiceActivo >= 0) {

			evento.preventDefault();

			inputRaza.value = opcionesActuales[indiceActivo].nombre;

			ocultarSugerencias();

		} else if (evento.key === "Escape") {

			ocultarSugerencias();

		}

	});

	// Cerrar al hacer clic fuera del campo
	document.addEventListener("click", evento => {

		if (!inputRaza.parentElement.contains(evento.target)) {
			ocultarSugerencias();
		}

	});

});
