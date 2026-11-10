document.addEventListener('DOMContentLoaded', function () {

    let formularioPendiente = null;

    const modalElemento = document.getElementById(
        'confirmarAccionMascotaModal'
    );

    const modal = bootstrap.Modal.getOrCreateInstance(modalElemento);

    const titulo = document.getElementById('tituloConfirmacion');
    const mensaje = document.getElementById('mensajeConfirmacion');
    const botonConfirmar = document.getElementById(
        'confirmarAccionMascota'
    );

    document.querySelectorAll('.pet-action-form').forEach(formulario => {

        formulario.addEventListener('submit', function (event) {
            event.preventDefault();

            formularioPendiente = this;

            titulo.textContent = this.dataset.confirmTitle;
            mensaje.textContent = this.dataset.confirmMessage;
            botonConfirmar.textContent = this.dataset.confirmButton;

            const bloquear = this.action.includes('/bloquear');

            botonConfirmar.className = bloquear
                ? 'btn btn-danger'
                : 'btn btn-success';

            modal.show();
        });

    });

    botonConfirmar.addEventListener('click', function () {

        if (formularioPendiente) {
            formularioPendiente.submit();
        }

    });

    modalElemento.addEventListener('hidden.bs.modal', function () {
        formularioPendiente = null;
    });

});