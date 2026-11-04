package com.lovePaws.mascota.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lovePaws.exception.DatoInvalidoException;
import com.lovePaws.exception.RecursoNoEncontradoException;

import com.lovePaws.mascota.dto.RegistrarMascotaDTO;
import com.lovePaws.mascota.dto.ActualizarMascotaDTO;
import com.lovePaws.mascota.dto.MascotaRespuestaDTO;
import com.lovePaws.mascota.entity.EstadoAdopcion;
import com.lovePaws.mascota.entity.EstadoPublicacion;
import com.lovePaws.mascota.entity.Mascota;
import com.lovePaws.mascota.entity.TipoMascota;
import com.lovePaws.mascota.entity.Raza;

import com.lovePaws.mascota.repository.MascotaRepository;
import com.lovePaws.mascota.repository.TipoMascotaRepository;
import com.lovePaws.solicitud.entity.EstadoSolicitud;
import com.lovePaws.solicitud.entity.EventoAdopcion;
import com.lovePaws.solicitud.entity.SolicitudAdopcion;
import com.lovePaws.solicitud.entity.TipoEventoAdopcion;
import com.lovePaws.solicitud.repository.EventoAdopcionRepository;
import com.lovePaws.solicitud.repository.SolicitudAdopcionRepository;
import com.lovePaws.mascota.repository.RazaRepository;

import com.lovePaws.usuario.entity.Usuario;
import com.lovePaws.usuario.repository.UsuarioRepository;

@Service
public class MascotaService {

	private final MascotaRepository mascotaRepository;
	private final TipoMascotaRepository tipoMascotaRepository;
	private final RazaRepository razaRepository;
	private final UsuarioRepository usuarioRepository;
	private final SolicitudAdopcionRepository solicitudRepository;
	private final EventoAdopcionRepository eventoRepository;

	public MascotaService(MascotaRepository mascotaRepository, TipoMascotaRepository tipoMascotaRepository,
			RazaRepository razaRepository, UsuarioRepository usuarioRepository,
			SolicitudAdopcionRepository solicitudRepository, EventoAdopcionRepository eventoRepository) {
		this.mascotaRepository = mascotaRepository;
		this.tipoMascotaRepository = tipoMascotaRepository;
		this.razaRepository = razaRepository;
		this.usuarioRepository = usuarioRepository;
		this.solicitudRepository = solicitudRepository;
		this.eventoRepository = eventoRepository;
	}

	@Transactional
	public MascotaRespuestaDTO registrarMascota(RegistrarMascotaDTO datos, String username) {

		// 1. Buscar el tipo de mascota
		TipoMascota tipo = tipoMascotaRepository.findById(datos.getIdTipoMascota())
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el tipo de mascota seleccionado."));

		// 2. Buscar y validar la raza, si fue seleccionada
		Raza raza = null;
		if (datos.getIdRaza() != null) {
			raza = razaRepository.findById(datos.getIdRaza())
					.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la raza seleccionada."));
			if (!raza.isActiva()) {
				throw new DatoInvalidoException("La raza seleccionada no está activa.");
			}
			if (!raza.getTipoMascota().getIdTipoMascota().equals(tipo.getIdTipoMascota())) {
				throw new DatoInvalidoException("La raza seleccionada no pertenece al tipo de mascota indicado.");
			}
		}

		// 3. Buscar al gestor autenticado
		Usuario gestor = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario autenticado."));

		// 4. Crear la entidad Mascota
		Mascota nuevaMascota = new Mascota(tipo, raza, datos.getNombre(), datos.getEdadAnios(), datos.getEdadMeses(),
				datos.getSexo(), datos.getDescripcion(), datos.getFotoUrl(), gestor);

		// 5. Guardar en MySQL
		Mascota mascotaGuardada = mascotaRepository.save(nuevaMascota);

		// 6. Convertir Entity a DTO de respuesta
		return convertirARespuesta(mascotaGuardada);
	}

	private MascotaRespuestaDTO convertirARespuesta(Mascota mascota) {
		return new MascotaRespuestaDTO(mascota.getIdMascota(), mascota.getNombre(),
				mascota.getTipoMascota().getNombre(), mascota.getRaza() != null ? mascota.getRaza().getNombre() : null,
				mascota.getEdadAnios(), mascota.getEdadMeses(), mascota.getSexo(), mascota.getDescripcion(),
				mascota.getFotoUrl(), mascota.getEstadoAdopcion(), mascota.getEstadoPublicacion(),
				mascota.getFechaRegistro());
	}

	@Transactional(readOnly = true)
	public List<MascotaRespuestaDTO> listarCatalogo() {
		List<Mascota> mascotas = mascotaRepository.findByEstadoPublicacionAndEstadoAdopcionOrderByFechaRegistroDesc(
				EstadoPublicacion.ACTIVA, EstadoAdopcion.DISPONIBLE);
		return mascotas.stream().map(this::convertirARespuesta).toList();
	}

	@Transactional
	public MascotaRespuestaDTO actualizarMascota(Long idMascota, ActualizarMascotaDTO datos, String username) {

		// 1. Buscar al gestor autenticado
		Usuario gestor = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario autenticado."));

		// 2. Buscar la mascota y comprobar que pertenezca al gestor
		Mascota mascota = mascotaRepository.findByIdMascotaAndRegistradoPor_IdUsuario(idMascota, gestor.getIdUsuario())
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"No se encontró la mascota o no tienes permiso para modificarla."));

		// 3. Buscar el nuevo tipo de mascota
		TipoMascota tipo = tipoMascotaRepository.findById(datos.getIdTipoMascota())
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el tipo de mascota seleccionado."));

		// 4. Buscar y validar la nueva raza
		Raza raza = null;
		if (datos.getIdRaza() != null) {
			raza = razaRepository.findById(datos.getIdRaza())
					.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la raza seleccionada."));
			if (!raza.isActiva()) {
				throw new DatoInvalidoException("La raza seleccionada no está activa.");
			}
			if (!raza.getTipoMascota().getIdTipoMascota().equals(tipo.getIdTipoMascota())) {
				throw new DatoInvalidoException("La raza seleccionada no pertenece al tipo de mascota indicado.");
			}
		}

		// 5. Actualizar los datos editables
		mascota.setTipoMascota(tipo);
		mascota.setRaza(raza);
		mascota.setNombre(datos.getNombre());
		mascota.setEdadAnios(datos.getEdadAnios());
		mascota.setEdadMeses(datos.getEdadMeses());
		mascota.setSexo(datos.getSexo());
		mascota.setDescripcion(datos.getDescripcion());
		mascota.setFotoUrl(datos.getFotoUrl());

		// 6. Registrar quién realizó la actualización
		mascota.setActualizadoPor(gestor);

		// 7. Guardar los cambios
		Mascota mascotaActualizada = mascotaRepository.save(mascota);

		// 8. Devolver el DTO de respuesta
		return convertirARespuesta(mascotaActualizada);
	}

	@Transactional(readOnly = true)
	public List<MascotaRespuestaDTO> listarMascotasGestor(String username) {

		// 1. Buscar al gestor autenticado
		Usuario gestor = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario autenticado."));

		// 2. Obtener las mascotas registradas por el gestor
		List<Mascota> mascotas = mascotaRepository
				.findByRegistradoPor_IdUsuarioOrderByFechaRegistroDesc(gestor.getIdUsuario());

		// 3. Convertir las entidades en DTO
		return mascotas.stream().map(this::convertirARespuesta).toList();
	}

	@Transactional(readOnly = true)
	public MascotaRespuestaDTO buscarMascotaGestor(Long idMascota, String username) {

		// 1. Buscar al gestor autenticado
		Usuario gestor = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario autenticado."));

		// 2. Buscar la mascota y comprobar que pertenezca al gestor
		Mascota mascota = mascotaRepository.findByIdMascotaAndRegistradoPor_IdUsuario(idMascota, gestor.getIdUsuario())
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"No se encontró la mascota o no tienes permiso para consultarla."));

		// 3. Convertir Entity a DTO
		return convertirARespuesta(mascota);
	}

	@Transactional(readOnly = true)
	public List<MascotaRespuestaDTO> listarTodasLasMascotas() {
		List<Mascota> mascotas = mascotaRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaRegistro"));
		return mascotas.stream().map(this::convertirARespuesta).toList();
	}

	@Transactional(readOnly = true)
	public MascotaRespuestaDTO buscarMascotaAdmin(Long idMascota) {
		Mascota mascota = mascotaRepository.findById(idMascota)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la mascota solicitada."));
		return convertirARespuesta(mascota);
	}

	@Transactional
	public MascotaRespuestaDTO bloquearMascota(Long idMascota, String username) {

		// 1. Buscar al administrador autenticado
		Usuario administrador = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario: " + username));

		// 2. Buscar y bloquear la mascota
		Mascota mascota = mascotaRepository.buscarPorIdConBloqueo(idMascota)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la mascota con ID: " + idMascota));

		// 3. Obtener las solicitudes pendientes de la mascota
		List<SolicitudAdopcion> solicitudesPendientes = solicitudRepository.buscarPorMascotaYEstadoConBloqueo(idMascota,
				EstadoSolicitud.PENDIENTE);
		LocalDateTime fechaResolucion = LocalDateTime.now();
		List<EventoAdopcion> eventos = new ArrayList<>();

		// 4. Rechazar automáticamente las solicitudes pendientes
		for (SolicitudAdopcion solicitud : solicitudesPendientes) {
			solicitud.setEstado(EstadoSolicitud.RECHAZADA);
			solicitud.setResueltoPor(administrador);
			solicitud.setFechaResolucion(fechaResolucion);
			solicitud.setObservacionResolucion("Solicitud rechazada automáticamente porque la mascota fue bloqueada.");

			// Crear evento histórico
			EventoAdopcion evento = new EventoAdopcion(solicitud, administrador,
					TipoEventoAdopcion.RECHAZO_AUTOMATICO_POR_BLOQUEO,
					"La mascota fue bloqueada por el administrador.");
			eventos.add(evento);
		}

		// 5. Cambiar el estado de publicación de la mascota
		mascota.setEstadoPublicacion(EstadoPublicacion.BLOQUEADA);

		// 6. Guardar todos los cambios
		solicitudRepository.saveAll(solicitudesPendientes);
		eventoRepository.saveAll(eventos);
		Mascota mascotaActualizada = mascotaRepository.save(mascota);

		// 7. Devolver la mascota actualizada
		return convertirARespuesta(mascotaActualizada);
	}

	@Transactional
	public MascotaRespuestaDTO desbloquearMascota(Long idMascota) {
		Mascota mascota = mascotaRepository.findById(idMascota)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la mascota con ID: " + idMascota));
		mascota.setEstadoPublicacion(EstadoPublicacion.ACTIVA);
		Mascota mascotaActualizada = mascotaRepository.save(mascota);
		return convertirARespuesta(mascotaActualizada);
	}

	@Transactional(readOnly = true)
	public List<MascotaRespuestaDTO> listarCatalogo(String tipo) {

		// Si no se especificó un tipo, mostrar todo el catálogo
		if (tipo == null || tipo.isBlank()) {
			return listarCatalogo();
		}

		// Consultar las mascotas según el tipo
		List<Mascota> mascotas = mascotaRepository
				.findByEstadoPublicacionAndEstadoAdopcionAndTipoMascota_NombreOrderByFechaRegistroDesc(
						EstadoPublicacion.ACTIVA, EstadoAdopcion.DISPONIBLE, tipo.trim().toUpperCase(Locale.ROOT));

		// Convertir las entidades en DTO
		return mascotas.stream().map(this::convertirARespuesta).toList();
	}
}