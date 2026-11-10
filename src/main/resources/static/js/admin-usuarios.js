document.addEventListener("DOMContentLoaded", () => {

	const modal = document.getElementById("modalAccionUsuario");

	if (!modal) return;

	const form = document.getElementById("formAccionUsuario");
	const titulo = document.getElementById("tituloModalUsuario");
	const mensaje = document.getElementById("mensajeModalUsuario");
	const nombreUsuario = document.getElementById("nombreUsuarioModal");
	const grupoMotivo = document.getElementById("grupoMotivo");
	const motivo = document.getElementById("motivoBloqueo");
	const botonConfirmar = document.getElementById("btnConfirmarUsuario");

	modal.addEventListener("show.bs.modal", (event) => {

		const boton = event.relatedTarget;

		const idUsuario = boton.dataset.userId;
		const username = boton.dataset.userName;
		const accion = boton.dataset.action;

		const esBloqueo = accion === "bloquear";

		form.action = `/admin/usuarios/${idUsuario}/${accion}`;

		nombreUsuario.textContent = `Usuario: ${username}`;

		titulo.textContent = esBloqueo
			? "Bloquear cuenta"
			: "Desbloquear cuenta";

		mensaje.textContent = esBloqueo
			? "Esta operación impedirá que el usuario acceda a su cuenta y rechazará sus solicitudes pendientes. ¿Deseas continuar?"
			: "El usuario podrá volver a iniciar sesión. Las solicitudes rechazadas anteriormente no serán restauradas.";

		grupoMotivo.hidden = !esBloqueo;
		motivo.required = esBloqueo;
		motivo.disabled = !esBloqueo;
		motivo.value = "";

		botonConfirmar.textContent = esBloqueo
			? "Confirmar bloqueo"
			: "Confirmar desbloqueo";

		botonConfirmar.className = esBloqueo
			? "btn btn-danger"
			: "btn btn-success";

	});

});