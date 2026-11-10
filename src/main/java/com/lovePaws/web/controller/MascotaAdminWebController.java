package com.lovePaws.web.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lovePaws.exception.DatoInvalidoException;
import com.lovePaws.exception.RecursoNoEncontradoException;
import com.lovePaws.mascota.dto.MascotaRespuestaDTO;
import com.lovePaws.mascota.entity.EstadoAdopcion;
import com.lovePaws.mascota.entity.EstadoPublicacion;
import com.lovePaws.mascota.service.MascotaService;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class MascotaAdminWebController {

	private final MascotaService mascotaService;

	public MascotaAdminWebController(MascotaService mascotaService) {
		this.mascotaService = mascotaService;
	}

	@GetMapping("/admin/mascotas")
	public String listarMascotas(@RequestParam(required = false) EstadoAdopcion estadoAdopcion,
			@RequestParam(required = false) EstadoPublicacion estadoPublicacion,
			@RequestParam(required = false) String tipo, Model model) {

		// 1. Obtener todas las mascotas registradas
		List<MascotaRespuestaDTO> mascotas = mascotaService.listarTodasLasMascotas();

		// 2. Filtrar por estado de adopción
		if (estadoAdopcion != null) {
			mascotas = mascotas.stream().filter(m -> m.getEstadoAdopcion() == estadoAdopcion).toList();
		}

		// 3. Filtrar por estado de publicación
		if (estadoPublicacion != null) {
			mascotas = mascotas.stream().filter(m -> m.getEstadoPublicacion() == estadoPublicacion).toList();
		}

		// 4. Filtrar por tipo de mascota
		String tipoSeleccionado = (tipo == null || tipo.isBlank()) ? "TODOS" : tipo.trim().toUpperCase(Locale.ROOT);

		if (!tipoSeleccionado.equals("TODOS")) {
			mascotas = mascotas.stream().filter(m -> m.getTipoMascota().equalsIgnoreCase(tipoSeleccionado)).toList();
		}

		// 5. Enviar los resultados a Thymeleaf
		model.addAttribute("mascotas", mascotas);
		model.addAttribute("estadoAdopcionSeleccionado", estadoAdopcion);
		model.addAttribute("estadoPublicacionSeleccionado", estadoPublicacion);
		model.addAttribute("tipoSeleccionado", tipoSeleccionado);
		model.addAttribute("totalMascotas", mascotas.size());

		return "admin/mascotas/listado";
	}

	@PostMapping("/admin/mascotas/{id}/bloquear")
	public String bloquearMascota(@PathVariable Long id, Authentication authentication,
			RedirectAttributes redirectAttributes) {

		try {
			mascotaService.bloquearMascota(id, authentication.getName());
			redirectAttributes.addFlashAttribute("success", "La mascota fue bloqueada correctamente.");

		} catch (DatoInvalidoException | RecursoNoEncontradoException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/admin/mascotas";
	}

	@PostMapping("/admin/mascotas/{id}/desbloquear")
	public String desbloquearMascota(@PathVariable Long id, RedirectAttributes redirectAttributes) {

		try {
			mascotaService.desbloquearMascota(id);
			redirectAttributes.addFlashAttribute("success", "La mascota fue desbloqueada correctamente.");

		} catch (DatoInvalidoException | RecursoNoEncontradoException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/admin/mascotas";
	}
}
