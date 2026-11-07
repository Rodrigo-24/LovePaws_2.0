package com.lovePaws.web.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lovePaws.exception.DatoInvalidoException;
import com.lovePaws.exception.RecursoNoEncontradoException;
import com.lovePaws.mascota.dto.ActualizarMascotaDTO;
import com.lovePaws.mascota.dto.MascotaRespuestaDTO;
import com.lovePaws.mascota.dto.RegistrarMascotaDTO;
import com.lovePaws.mascota.entity.EstadoAdopcion;
import com.lovePaws.mascota.entity.EstadoPublicacion;
import com.lovePaws.mascota.entity.Raza;
import com.lovePaws.mascota.repository.RazaRepository;
import com.lovePaws.mascota.repository.TipoMascotaRepository;
import com.lovePaws.mascota.service.MascotaService;

import jakarta.validation.Valid;

@Controller
@PreAuthorize("hasRole('GESTOR')")
public class MascotaWebController {

	private final MascotaService mascotaService;
	private final TipoMascotaRepository tipoMascotaRepository;
	private final RazaRepository razaRepository;

	public MascotaWebController(MascotaService mascotaService, TipoMascotaRepository tipoMascotaRepository,
			RazaRepository razaRepository) {
		this.mascotaService = mascotaService;
		this.tipoMascotaRepository = tipoMascotaRepository;
		this.razaRepository = razaRepository;
	}

	// Mostrar formulario de registro
	@GetMapping("/gestor/mascotas/nueva")
	public String mostrarFormulario(Model model) {
		model.addAttribute("mascota", new RegistrarMascotaDTO());
		cargarCatalogos(model);
		return "gestor/mascotas/formulario";
	}

	// Registrar mascota desde el formulario web
	@PostMapping("/gestor/mascotas")
	public String registrarMascota(@Valid @ModelAttribute("mascota") RegistrarMascotaDTO datos,
			BindingResult bindingResult, @RequestParam(value = "fotografia", required = false) MultipartFile fotografia,
			Authentication authentication, Model model, RedirectAttributes redirectAttributes) {

		// 1. Comprobar las validaciones del DTO
		if (bindingResult.hasErrors()) {
			cargarCatalogos(model);
			return "gestor/mascotas/formulario";
		}

		// 2. Registrar la mascota y su fotografía
		mascotaService.registrarMascota(datos, fotografia, authentication.getName());

		// 3. Mostrar mensaje de éxito
		redirectAttributes.addFlashAttribute("mensajeExito", "La mascota se registró correctamente.");
		return "redirect:/gestor/dashboard";
	}

	// Mostrar formulario de edición
	@GetMapping("/gestor/mascotas/{id}/editar")
	public String mostrarFormularioEdicion(@PathVariable Long id, Authentication authentication, Model model) {

		// 1. Obtener los datos actuales de la mascota
		ActualizarMascotaDTO datos = mascotaService.obtenerDatosEdicion(id, authentication.getName());

		// 2. Enviar los datos a Thymeleaf
		model.addAttribute("mascota", datos);
		model.addAttribute("idMascota", id);
		model.addAttribute("fotoActual", datos.getFotoUrl());
		model.addAttribute("modoEdicion", true);

		// 3. Cargar los tipos y razas
		cargarCatalogos(model);
		return "gestor/mascotas/formulario";
	}

	// Actualizar mascota desde el formulario web
	@PostMapping("/gestor/mascotas/{id}/editar")
	public String actualizarMascota(@PathVariable Long id, @Valid @ModelAttribute("mascota") ActualizarMascotaDTO datos,
			BindingResult bindingResult, @RequestParam(value = "fotografia", required = false) MultipartFile fotografia,
			Authentication authentication, Model model, RedirectAttributes redirectAttributes) {

		// 1. Validar los datos del formulario
		if (bindingResult.hasErrors()) {
			cargarCatalogos(model);
			model.addAttribute("idMascota", id);
			model.addAttribute("fotoActual", datos.getFotoUrl());
			model.addAttribute("modoEdicion", true);
			return "gestor/mascotas/formulario";
		}
		try {
			// 2. Actualizar los datos de la mascota
			mascotaService.actualizarMascota(id, datos, fotografia, authentication.getName());

			// 3. Comunicar el resultado
			redirectAttributes.addFlashAttribute("mensajeExito", "La mascota se actualizó correctamente.");
			return "redirect:/gestor/mascotas";
		} catch (DatoInvalidoException ex) {

			// 4. Mostrar errores relacionados con los datos
			cargarCatalogos(model);
			model.addAttribute("idMascota", id);
			model.addAttribute("fotoActual", datos.getFotoUrl());
			model.addAttribute("modoEdicion", true);
			model.addAttribute("mensajeError", ex.getMessage());
			return "gestor/mascotas/formulario";
		} catch (RecursoNoEncontradoException ex) {

			// 5. Si no existe la mascota o no pertenece al gestor
			redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
			return "redirect:/gestor/mascotas";
		}
	}

	// Consultar las mascotas registradas por el gestor
	@GetMapping("/gestor/mascotas")
	public String listarMascotas(@RequestParam(required = false) EstadoAdopcion estadoAdopcion,
			@RequestParam(required = false) EstadoPublicacion estadoPublicacion, Authentication authentication, Model model) {
		cargarResultadosMascotas(authentication.getName(), estadoAdopcion, estadoPublicacion, model);
		return "gestor/mascotas/listado";
	}

	// Consultar los resultados parciales del inventario mediante Fetch
	@GetMapping("/gestor/mascotas/resultados")
	public String resultadosMascotas(@RequestParam(required = false) EstadoAdopcion estadoAdopcion,
			@RequestParam(required = false) EstadoPublicacion estadoPublicacion, Authentication authentication, Model model) {
		cargarResultadosMascotas(authentication.getName(), estadoAdopcion, estadoPublicacion, model);
		return "gestor/mascotas/listado :: resultados";
	}

	// Cargar y filtrar las mascotas del gestor
	private void cargarResultadosMascotas(String username, EstadoAdopcion estadoAdopcion,
			EstadoPublicacion estadoPublicacion, Model model) {

		// 1. Obtener las mascotas del gestor autenticado
		List<MascotaRespuestaDTO> mascotas = mascotaService.listarMascotasGestor(username);

		// 2. Filtrar por estado de adopción
		if (estadoAdopcion != null) {
			mascotas = mascotas.stream().filter(m -> m.getEstadoAdopcion() == estadoAdopcion).toList();
		}

		// 3. Filtrar por estado de publicación
		if (estadoPublicacion != null) {
			mascotas = mascotas.stream().filter(m -> m.getEstadoPublicacion() == estadoPublicacion).toList();
		}

		// 4. Enviar información a Thymeleaf
		model.addAttribute("mascotas", mascotas);
		model.addAttribute("estadoAdopcionSeleccionado", estadoAdopcion);
		model.addAttribute("estadoPublicacionSeleccionado", estadoPublicacion);
	}

	// Cargar los datos necesarios para los selectores
	private void cargarCatalogos(Model model) {
		model.addAttribute("tiposMascota", tipoMascotaRepository.findAll());
		model.addAttribute("razas", razaRepository.findAll().stream().filter(Raza::isActiva).toList());
	}
}