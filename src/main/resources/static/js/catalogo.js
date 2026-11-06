
document.addEventListener("click", async (event) => {

	// Identificar si se hizo clic en un filtro del catálogo
	const filtro = event.target.closest("[data-catalogo-filtro]");

	if (!filtro) {
		return;
	}

	// Permitir abrir enlaces normalmente en otra pestaña
	if (
		event.button !== 0 ||
		event.ctrlKey ||
		event.shiftKey ||
		event.altKey ||
		event.metaKey
	) {
		return;
	}

	event.preventDefault();

	const tipo = filtro.dataset.tipo;
	const urlEndpoint = filtro.dataset.url;
	const urlNavegacion = filtro.href;

	await cargarResultados(tipo, urlEndpoint, urlNavegacion, true);

});


async function cargarResultados(
	tipo,
	urlEndpoint,
	urlNavegacion,
	actualizarHistorial
) {

	const catalogoActual = document.getElementById("catalogo");

	if (!catalogoActual) {
		return;
	}

	try {

		// Mostrar indicador de carga
		catalogoActual.classList.add("catalog-loading");
		catalogoActual.setAttribute("aria-busy", "true");

		// Construir la URL de consulta
		const url = new URL(urlEndpoint, window.location.origin);

		url.searchParams.set("tipo", tipo);

		// Realizar la petición sin recargar la página
		const response = await fetch(url, {
			method: "GET",
			headers: {
				"Accept": "text/html"
			}
		});

		if (!response.ok) {
			throw new Error("No se pudieron obtener las mascotas.");
		}

		// Obtener el HTML recibido
		const html = await response.text();

		// Interpretar el fragmento HTML
		const parser = new DOMParser();
		const documento = parser.parseFromString(html, "text/html");

		const nuevoCatalogo = documento.querySelector("#catalogo");

		if (!nuevoCatalogo) {
			throw new Error("No se encontró el fragmento del catálogo.");
		}

		// Reemplazar únicamente el catálogo actual
		catalogoActual.replaceWith(nuevoCatalogo);

		// Actualizar la URL sin recargar la página
		if (actualizarHistorial) {
			window.history.pushState({}, "", urlNavegacion);
		}

	} catch (error) {

		console.error("Error al filtrar el catálogo:", error);

		// Recuperar la navegación tradicional si falla Fetch
		window.location.href = urlNavegacion;

	}

}
