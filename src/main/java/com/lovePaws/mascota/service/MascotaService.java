package com.lovePaws.mascota.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
	private final FotoMascotaService fotoMascotaService;

	public MascotaService(MascotaRepository mascotaRepository, TipoMascotaRepository tipoMascotaRepository,
			RazaRepository razaRepository, UsuarioRepository usuarioRepository,
			SolicitudAdopcionRepository solicitudRepository, EventoAdopcionRepository eventoRepository,
			FotoMascotaService fotoMascotaService) {
		this.mascotaRepository = mascotaRepository;
		this.tipoMascotaRepository = tipoMascotaRepository;
		this.razaRepository = razaRepository;
		this.usuarioRepository = usuarioRepository;
		this.solicitudRepository = solicitudRepository;
		this.eventoRepository = eventoRepository;
		this.fotoMascotaService = fotoMascotaService;
	}

	// Registro mediante API REST (JSON)
	@Transactional
	public MascotaRespuestaDTO registrarMascota(RegistrarMascotaDTO datos, String username) {
		return registrarMascotaInterno(datos, username, datos.getFotoUrl());
	}

	// Registro mediante formulario web (MultipartFile)
	@Transactional
	public MascotaRespuestaDTO registrarMascota(RegistrarMascotaDTO datos, MultipartFile fotografia, String username) {

		// 1. Almacenar la fotografía
		String rutaFoto = fotoMascotaService.guardar(fotografia);

		// 2. Registrar una acción para cuando termine la transacción
		if (rutaFoto != null) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

				@Override
				public void afterCompletion(int status) {
					// Si la transacción no se confirmó,
					// eliminar la fotografía almacenada
					if (status != STATUS_COMMITTED) {
						fotoMascotaService.eliminar(rutaFoto);
					}
				}
			});
		}

		// 3. Ejecutar el registro normal de la mascota
		return registrarMascotaInterno(datos, username, rutaFoto);
	}

	// Lógica común del registro
	private MascotaRespuestaDTO registrarMascotaInterno(RegistrarMascotaDTO datos, String username, String fotoUrl) {

		// 1. Buscar el tipo de mascota
		TipoMascota tipo = tipoMascotaRepository.findById(datos.getIdTipoMascota())
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el tipo de mascota seleccionado."));

		// 2. Buscar o registrar la raza
		Raza raza = resolverRaza(tipo, datos.getIdRaza(), datos.getRaza());

		// 3. Buscar al gestor autenticado
		Usuario gestor = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario autenticado."));

		// 4. Crear la entidad Mascota
		Mascota nuevaMascota = new Mascota(tipo, raza, datos.getNombre(), datos.getEdadAnios(), datos.getEdadMeses(),
				datos.getSexo(), datos.getDescripcion(), fotoUrl, gestor);

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

	// Actualización mediante API REST
	@Transactional
	public MascotaRespuestaDTO actualizarMascota(Long idMascota, ActualizarMascotaDTO datos, String username) {
		return actualizarMascotaInterno(idMascota, datos, null, username, false);
	}

	// Actualización mediante formulario web
	@Transactional
	public MascotaRespuestaDTO actualizarMascota(Long idMascota, ActualizarMascotaDTO datos, MultipartFile fotografia,
			String username) {
		return actualizarMascotaInterno(idMascota, datos, fotografia, username, true);
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
	public ActualizarMascotaDTO obtenerDatosEdicion(Long idMascota, String username) {

		// 1. Buscar al gestor autenticado
		Usuario gestor = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario autenticado."));

		// 2. Buscar la mascota y verificar su propietario
		Mascota mascota = mascotaRepository.findByIdMascotaAndRegistradoPor_IdUsuario(idMascota, gestor.getIdUsuario())
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"No se encontró la mascota o no tienes permiso para modificarla."));

		// 3. Construir el DTO con los datos actuales
		ActualizarMascotaDTO datos = new ActualizarMascotaDTO();
		datos.setIdTipoMascota(mascota.getTipoMascota().getIdTipoMascota());
		datos.setIdRaza(mascota.getRaza() != null ? mascota.getRaza().getIdRaza() : null);
		datos.setRaza(mascota.getRaza() != null ? mascota.getRaza().getNombre() : null);
		datos.setNombre(mascota.getNombre());
		datos.setEdadAnios(mascota.getEdadAnios());
		datos.setEdadMeses(mascota.getEdadMeses());
		datos.setSexo(mascota.getSexo());
		datos.setDescripcion(mascota.getDescripcion());
		datos.setFotoUrl(mascota.getFotoUrl());
		return datos;
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

	private Raza resolverRaza(TipoMascota tipo, Long idRaza, String nombreRaza) {

		// Caso 1. El formulario web envía el nombre de la raza
		if (nombreRaza != null && !nombreRaza.isBlank()) {

			// Normalizar el nombre
			String nombreNormalizado = nombreRaza.trim().replaceAll("\\s+", " ");

			// Buscar si ya existe para el tipo de mascota seleccionado
			Raza raza = razaRepository
					.findByTipoMascota_IdTipoMascotaAndNombreIgnoreCase(tipo.getIdTipoMascota(), nombreNormalizado)
					.orElseGet(() -> razaRepository.save(new Raza(tipo, nombreNormalizado)));

			if (!raza.isActiva()) {
				throw new DatoInvalidoException("La raza indicada existe, pero actualmente está desactivada.");
			}
			return raza;
		}

		// Caso 2. La API REST envía el ID de una raza existente
		if (idRaza != null) {
			Raza raza = razaRepository.findById(idRaza)
					.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la raza seleccionada."));
			if (!raza.isActiva()) {
				throw new DatoInvalidoException("La raza seleccionada no está activa.");
			}
			if (!raza.getTipoMascota().getIdTipoMascota().equals(tipo.getIdTipoMascota())) {
				throw new DatoInvalidoException("La raza seleccionada no pertenece al tipo de mascota indicado.");
			}
			return raza;
		}
		// Caso 3. No se indicó ninguna raza
		return null;
	}

	// Lógica común de actualización
	private MascotaRespuestaDTO actualizarMascotaInterno(Long idMascota, ActualizarMascotaDTO datos,
			MultipartFile fotografia, String username, boolean actualizarFotoPorArchivo) {

		// 1. Buscar al gestor autenticado
		Usuario gestor = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario autenticado."));

		// 2. Buscar la mascota y verificar que le pertenezca
		Mascota mascota = mascotaRepository.findByIdMascotaAndRegistradoPor_IdUsuario(idMascota, gestor.getIdUsuario())
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"No se encontró la mascota o no tienes permiso para modificarla."));

		// 3. Buscar el nuevo tipo de mascota
		TipoMascota tipo = tipoMascotaRepository.findById(datos.getIdTipoMascota())
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el tipo de mascota seleccionado."));

		// 4. Buscar o registrar la nueva raza
		Raza raza = resolverRaza(tipo, datos.getIdRaza(), datos.getRaza());

		// 5. Gestionar la fotografía
		String fotoAnterior = mascota.getFotoUrl();
		String fotoFinal;
		if (actualizarFotoPorArchivo) {
			String rutaFotoNueva = fotoMascotaService.guardar(fotografia);
			// Si no se seleccionó una nueva fotografía,
			// conservar la fotografía anterior.
			fotoFinal = rutaFotoNueva != null ? rutaFotoNueva : fotoAnterior;

			// Registrar la limpieza de archivos
			if (rutaFotoNueva != null) {
				TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
					@Override
					public void afterCompletion(int status) {
						if (status != STATUS_COMMITTED) {
							// Si falla la transacción,
							// eliminar la nueva fotografía.
							fotoMascotaService.eliminar(rutaFotoNueva);
						} else if (fotoAnterior != null && !fotoAnterior.equals(rutaFotoNueva)) {
							// Si se confirmó la transacción,
							// eliminar la fotografía anterior.
							fotoMascotaService.eliminar(fotoAnterior);
						}
					}
				});
			}
		} else {
			// Mantener el comportamiento del endpoint REST
			fotoFinal = datos.getFotoUrl();
		}
		// 6. Actualizar los datos editables
		mascota.setTipoMascota(tipo);
		mascota.setRaza(raza);
		mascota.setNombre(datos.getNombre());
		mascota.setEdadAnios(datos.getEdadAnios());
		mascota.setEdadMeses(datos.getEdadMeses());
		mascota.setSexo(datos.getSexo());
		mascota.setDescripcion(datos.getDescripcion());
		mascota.setFotoUrl(fotoFinal);

		// 7. Registrar quién realizó la actualización
		mascota.setActualizadoPor(gestor);

		// 8. Guardar los cambios
		Mascota mascotaActualizada = mascotaRepository.save(mascota);

		// 9. Devolver el DTO
		return convertirARespuesta(mascotaActualizada);
	}

	@Transactional(readOnly = true)
	public MascotaRespuestaDTO buscarMascotaDisponible(Long idMascota) {

		// 1. Buscar la mascota
		Mascota mascota = mascotaRepository.findById(idMascota)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la mascota solicitada."));

		// 2. Verificar que esté publicada y disponible
		if (mascota.getEstadoPublicacion() != EstadoPublicacion.ACTIVA
				|| mascota.getEstadoAdopcion() != EstadoAdopcion.DISPONIBLE) {
			throw new DatoInvalidoException("Esta mascota ya no está disponible para adopción.");
		}

		// 3. Convertir a DTO
		return convertirARespuesta(mascota);
	}
}