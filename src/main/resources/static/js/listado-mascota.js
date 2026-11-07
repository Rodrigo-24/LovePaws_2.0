// Identificar los clics en los filtros del inventario
document.addEventListener("click", async (event) => {

	const filtro = event.target.closest("[data-inventario-filtro]");

	if (!filtro) {
		return;
	}

	// Permitir abrir enlaces en otra pestaña
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

	const urlEndpoint = filtro.dataset.url;
	const urlNavegacion = filtro.href;

	await cargarResultados(urlEndpoint, urlNavegacion, true);

});


// Solicitar y actualizar los resultados del inventario
let numeroPeticion = 0;

async function cargarResultados(
	urlEndpoint,
	urlNavegacion,
	actualizarHistorial
) {

	const inventarioActual = document.getElementById("resultadosMascotas");

	if (!inventarioActual) {
		return;
	}

	const peticionActual = ++numeroPeticion;

	// Preparar las URL
	const url = new URL(urlEndpoint, window.location.origin);
	const urlDestino = new URL(urlNavegacion, window.location.origin);

	// Conservar los parámetros del filtro seleccionado
	url.search = urlDestino.search;

	try {

		// Mostrar indicador de carga
		inventarioActual.classList.add("inventory-loading");
		inventarioActual.setAttribute("aria-busy", "true");

		// Solicitar los resultados al servidor
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

		// Interpretar el fragmento
		const parser = new DOMParser();
		const documento = parser.parseFromString(html, "text/html");

		const nuevoInventario =
			documento.querySelector("#resultadosMascotas");

		if (!nuevoInventario) {
			throw new Error("No se encontró el fragmento del inventario.");
		}

		// Evitar reemplazar con una respuesta anterior
		if (peticionActual !== numeroPeticion) {
			return;
		}

		// Reemplazar únicamente los resultados
		inventarioActual.replaceWith(nuevoInventario);

		// Actualizar la URL sin recargar la página
		if (actualizarHistorial) {
			window.history.pushState({}, "", urlDestino);
		}

	} catch (error) {

		console.error("Error al filtrar el inventario:", error);

		// Recuperar la navegación tradicional si Fetch falla
		if (peticionActual === numeroPeticion) {
			window.location.href = urlDestino.href;
		}

	}

}


// Gestionar los botones Atrás y Adelante del navegador
window.addEventListener("popstate", () => {

	cargarResultados(
		"/gestor/mascotas/resultados",
		window.location.href,
		false
	);

});