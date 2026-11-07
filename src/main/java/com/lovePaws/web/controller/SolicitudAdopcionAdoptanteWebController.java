package com.lovePaws.web.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.lovePaws.exception.DatoInvalidoException;
import com.lovePaws.exception.RecursoNoEncontradoException;
import com.lovePaws.mascota.dto.MascotaRespuestaDTO;
import com.lovePaws.mascota.service.MascotaService;
import com.lovePaws.solicitud.dto.CrearSolicitudDTO;
import com.lovePaws.solicitud.dto.PreguntaSolicitudDTO;
import com.lovePaws.solicitud.dto.RespuestaSolicitudDTO;
import com.lovePaws.solicitud.service.SolicitudAdopcionService;

@Controller
@PreAuthorize("hasRole('ADOPTANTE')")
public class SolicitudAdopcionAdoptanteWebController {

	private final MascotaService mascotaService;
	private final SolicitudAdopcionService solicitudService;

	public SolicitudAdopcionAdoptanteWebController(MascotaService mascotaService,
			SolicitudAdopcionService solicitudService) {
		this.mascotaService = mascotaService;
		this.solicitudService = solicitudService;
	}

	// Mostrar el formulario de solicitud de adopción
	@GetMapping("/adoptante/mascotas/{idMascota}/solicitar")
	public String mostrarFormulario(@PathVariable Long idMascota, Model model, RedirectAttributes redirectAttributes) {
		try {
			// 1. Buscar la mascota y comprobar que esté disponible
			MascotaRespuestaDTO mascota = mascotaService.buscarMascotaDisponible(idMascota);

			// 2. Obtener las preguntas activas
			List<PreguntaSolicitudDTO> preguntas = solicitudService.listarPreguntasActivas();

			// 3. Preparar el DTO de la solicitud
			CrearSolicitudDTO datos = new CrearSolicitudDTO();
			datos.setIdMascota(idMascota);

			// 4. Preparar las respuestas vacías
			List<RespuestaSolicitudDTO> respuestas = new ArrayList<>();
			for (PreguntaSolicitudDTO pregunta : preguntas) {
				RespuestaSolicitudDTO respuesta = new RespuestaSolicitudDTO();
				respuesta.setIdPregunta(pregunta.getIdPregunta());
				respuesta.setRespuesta("");
				respuestas.add(respuesta);
			}
			datos.setRespuestas(respuestas);

			// 5. Enviar información a Thymeleaf
			model.addAttribute("mascota", mascota);
			model.addAttribute("preguntas", preguntas);
			model.addAttribute("solicitud", datos);
			return "adoptante/solicitud-formulario";

		} catch (DatoInvalidoException | RecursoNoEncontradoException ex) {
			redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
			return "redirect:/adoptante/catalogo";
		}
	}

	// Registrar una solicitud de adopción desde el formulario web
	@PostMapping("/adoptante/mascotas/{idMascota}/solicitar")
	public String enviarSolicitud(@PathVariable Long idMascota,
			@Valid @ModelAttribute("solicitud") CrearSolicitudDTO datos, BindingResult bindingResult,
			Authentication authentication, Model model, RedirectAttributes redirectAttributes) {

		// 1. Utilizar el ID de la URL como referencia de la mascota
		datos.setIdMascota(idMascota);

		// 2. Comprobar las validaciones del DTO
		if (bindingResult.hasErrors()) {

			return volverAlFormulario(idMascota, "Revisa los datos enviados e intenta nuevamente.", model,
					redirectAttributes);
		}
		try {

			// 3. Registrar la solicitud mediante el servicio
			solicitudService.crearSolicitud(datos, authentication.getName());

			// 4. Comunicar el resultado
			redirectAttributes.addFlashAttribute("mensajeExito",
					"Tu solicitud de adopción fue registrada correctamente.");

			// 5. Dirigir al adoptante a sus solicitudes
			return "redirect:/adoptante/mis-adopciones";

		} catch (DatoInvalidoException ex) {

			return volverAlFormulario(idMascota, ex.getMessage(), model, redirectAttributes);

		} catch (RecursoNoEncontradoException ex) {
			redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());

			return "redirect:/adoptante/catalogo";
		}
	}

	// Recuperar el formulario cuando ocurre un error
	private String volverAlFormulario(Long idMascota, String mensaje, Model model, RedirectAttributes redirectAttributes) {
		try {
			// Volver a comprobar que la mascota siga disponible
			MascotaRespuestaDTO mascota = mascotaService.buscarMascotaDisponible(idMascota);

			// Recuperar las preguntas activas
			List<PreguntaSolicitudDTO> preguntas = solicitudService.listarPreguntasActivas();

			model.addAttribute("mascota", mascota);
			model.addAttribute("preguntas", preguntas);
			model.addAttribute("mensajeError", mensaje);

			return "adoptante/solicitud-formulario";

		} catch (DatoInvalidoException | RecursoNoEncontradoException ex) {
			redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());

			return "redirect:/adoptante/catalogo";
		}
	}
}