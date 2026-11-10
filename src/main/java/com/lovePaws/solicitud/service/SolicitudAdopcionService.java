package com.lovePaws.solicitud.service;

import com.lovePaws.mascota.entity.Mascota;
import com.lovePaws.mascota.entity.EstadoAdopcion;
import com.lovePaws.mascota.entity.EstadoPublicacion;
import com.lovePaws.mascota.repository.MascotaRepository;
import com.lovePaws.usuario.entity.EstadoUsuario;
import com.lovePaws.usuario.entity.Usuario;
import com.lovePaws.usuario.repository.UsuarioRepository;
import com.lovePaws.solicitud.dto.CrearSolicitudDTO;
import com.lovePaws.solicitud.dto.EventoAdopcionDTO;
import com.lovePaws.solicitud.dto.PreguntaSolicitudDTO;
import com.lovePaws.solicitud.dto.ResolverSolicitudDTO;
import com.lovePaws.solicitud.dto.RespuestaSolicitudDTO;
import com.lovePaws.solicitud.dto.RespuestaSolicitudDetalleDTO;
import com.lovePaws.solicitud.dto.SolicitudAdopcionDetalleDTO;
import com.lovePaws.solicitud.dto.SolicitudAdopcionGestorDTO;
import com.lovePaws.solicitud.dto.SolicitudAdopcionRespuestaDTO;
import com.lovePaws.solicitud.entity.EstadoSolicitud;
import com.lovePaws.solicitud.entity.EventoAdopcion;
import com.lovePaws.solicitud.entity.PreguntaSolicitud;
import com.lovePaws.solicitud.entity.RespuestaSolicitud;
import com.lovePaws.solicitud.entity.SolicitudAdopcion;
import com.lovePaws.solicitud.entity.TipoEventoAdopcion;
import com.lovePaws.solicitud.repository.EventoAdopcionRepository;
import com.lovePaws.solicitud.repository.PreguntaSolicitudRepository;
import com.lovePaws.solicitud.repository.RespuestaSolicitudRepository;
import com.lovePaws.solicitud.repository.SolicitudAdopcionRepository;
import com.lovePaws.exception.DatoInvalidoException;
import com.lovePaws.exception.RecursoNoEncontradoException;

import tools.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SolicitudAdopcionService {

	private final SolicitudAdopcionRepository solicitudRepository;
	private final MascotaRepository mascotaRepository;
	private final UsuarioRepository usuarioRepository;
	private final EventoAdopcionRepository eventoRepository;
	private final PreguntaSolicitudRepository preguntaSolicitudRepository;
	private final RespuestaSolicitudRepository respuestaSolicitudRepository;

	public SolicitudAdopcionService(SolicitudAdopcionRepository solicitudRepository,
			MascotaRepository mascotaRepository, UsuarioRepository usuarioRepository,
			EventoAdopcionRepository eventoAdopcionRepository, PreguntaSolicitudRepository preguntaSolicitudRepository,
			RespuestaSolicitudRepository respuestaSolicitudRepository) {
		this.solicitudRepository = solicitudRepository;
		this.mascotaRepository = mascotaRepository;
		this.usuarioRepository = usuarioRepository;
		this.eventoRepository = eventoAdopcionRepository;
		this.preguntaSolicitudRepository = preguntaSolicitudRepository;
		this.respuestaSolicitudRepository = respuestaSolicitudRepository;
	}

	@Transactional
	public SolicitudAdopcionRespuestaDTO crearSolicitud(CrearSolicitudDTO datos, String username) {
		Long idMascota = datos.getIdMascota();

		// 1. Buscar la mascota
		Mascota mascota = mascotaRepository.buscarPorIdConBloqueo(idMascota)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la mascota con ID: " + idMascota));

		// 2. Verificar que esté publicada y disponible
		if (mascota.getEstadoPublicacion() != EstadoPublicacion.ACTIVA
				|| mascota.getEstadoAdopcion() != EstadoAdopcion.DISPONIBLE) {
			throw new DatoInvalidoException("La mascota no está disponible para recibir solicitudes.");
		}

		// Verificar que el gestor responsable tenga una cuenta activa
		if (mascota.getRegistradoPor().getEstado() != EstadoUsuario.ACTIVO) {
			throw new DatoInvalidoException("La mascota no está disponible para recibir solicitudes.");
		}

		// 3. Buscar al adoptante autenticado
		Usuario adoptante = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario: " + username));

		// Verificar que el adoptante tenga una cuenta activa
		if (adoptante.getEstado() != EstadoUsuario.ACTIVO) {
			throw new DatoInvalidoException("Tu cuenta no se encuentra activa y no puedes enviar solicitudes.");
		}

		// 4. Comprobar solicitudes pendientes duplicadas
		boolean existeSolicitud = solicitudRepository.existsByMascota_IdMascotaAndAdoptante_IdUsuarioAndEstado(
				idMascota, adoptante.getIdUsuario(), EstadoSolicitud.PENDIENTE);
		if (existeSolicitud) {
			throw new DatoInvalidoException("Ya tienes una solicitud pendiente para esta mascota.");
		}

		// 5. Obtener las preguntas activas
		List<PreguntaSolicitud> preguntasActivas = preguntaSolicitudRepository.findByActivaTrueOrderByOrdenAsc();

		// Crear un mapa para localizar las preguntas por su ID
		Map<Long, PreguntaSolicitud> preguntasPorId = new HashMap<>();

		for (PreguntaSolicitud pregunta : preguntasActivas) {
			preguntasPorId.put(pregunta.getIdPregunta(), pregunta);
		}

		// 6. Validar las respuestas recibidas
		Set<Long> preguntasRecibidas = new HashSet<>();
		Set<Long> preguntasRespondidas = new HashSet<>();

		for (RespuestaSolicitudDTO respuestaDTO : datos.getRespuestas()) {
			Long idPregunta = respuestaDTO.getIdPregunta();

			// Evitar respuestas duplicadas
			if (!preguntasRecibidas.add(idPregunta)) {
				throw new DatoInvalidoException("No puedes responder dos veces la misma pregunta.");
			}

			// Buscar la pregunta y comprobar que esté activa
			PreguntaSolicitud pregunta = preguntasPorId.get(idPregunta);

			if (pregunta == null) {
				throw new DatoInvalidoException("La pregunta con ID " + idPregunta + " no existe o no está activa.");
			}
			String valor = respuestaDTO.getRespuesta();

			// Comprobar si la respuesta está vacía
			if (valor == null || valor.isBlank()) {

				// Solo se permite dejar vacías las preguntas opcionales
				if (pregunta.isObligatoria()) {
					throw new DatoInvalidoException("Debes responder la pregunta: " + pregunta.getTexto());
				}
				continue;
			}

			// Validar el formato de las respuestas contestadas
			validarFormatoRespuesta(pregunta, valor);
			preguntasRespondidas.add(idPregunta);
		}

		// 7. Comprobar que todas las preguntas obligatorias estén respondidas
		for (PreguntaSolicitud pregunta : preguntasActivas) {
			if (pregunta.isObligatoria() && !preguntasRespondidas.contains(pregunta.getIdPregunta())) {
				throw new DatoInvalidoException("Debes responder la pregunta: " + pregunta.getTexto());
			}
		}

		// 8. Crear y guardar la solicitud
		SolicitudAdopcion solicitud = new SolicitudAdopcion(mascota, adoptante);
		SolicitudAdopcion solicitudGuardada = solicitudRepository.save(solicitud);

		// 9. Registrar el evento de creación
		EventoAdopcion evento = new EventoAdopcion(solicitudGuardada, adoptante, TipoEventoAdopcion.SOLICITUD_CREADA,
				"El adoptante registró una solicitud de adopción.");
		eventoRepository.save(evento);

		// 10. Guardar únicamente las respuestas contestadas
		List<RespuestaSolicitud> respuestas = new ArrayList<>();
		for (RespuestaSolicitudDTO respuestaDTO : datos.getRespuestas()) {
			String valor = respuestaDTO.getRespuesta();

			// No guardar preguntas opcionales sin respuesta
			if (valor == null || valor.isBlank()) {
				continue;
			}
			PreguntaSolicitud pregunta = preguntasPorId.get(respuestaDTO.getIdPregunta());
			RespuestaSolicitud respuesta = new RespuestaSolicitud(solicitudGuardada, pregunta, pregunta.getTexto(),
					valor.trim(), pregunta.getOrden());
			respuestas.add(respuesta);
		}
		respuestaSolicitudRepository.saveAll(respuestas);

		return new SolicitudAdopcionRespuestaDTO(solicitudGuardada.getIdSolicitud(),
				solicitudGuardada.getMascota().getIdMascota(), solicitudGuardada.getMascota().getNombre(),
				solicitudGuardada.getMascota().getTipoMascota().getNombre(), solicitudGuardada.getFechaSolicitud(),
				solicitudGuardada.getEstado());
	}

	@Transactional(readOnly = true)
	public List<SolicitudAdopcionRespuestaDTO> listarSolicitudesAdoptante(String username) {

		// 1. Buscar al adoptante
		Usuario adoptante = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario: " + username));

		// 2. Obtener sus solicitudes
		List<SolicitudAdopcion> solicitudes = solicitudRepository
				.findByAdoptante_IdUsuarioOrderByFechaSolicitudDesc(adoptante.getIdUsuario());

		// 3. Convertir las entidades en DTO
		return solicitudes.stream()
				.map(solicitud -> new SolicitudAdopcionRespuestaDTO(solicitud.getIdSolicitud(),
						solicitud.getMascota().getIdMascota(), solicitud.getMascota().getNombre(),
						solicitud.getMascota().getTipoMascota().getNombre(), solicitud.getFechaSolicitud(),
						solicitud.getEstado()))
				.toList();
	}

	@Transactional(readOnly = true)
	public SolicitudAdopcionDetalleDTO obtenerDetalleSolicitud(Long idSolicitud, String username) {

		// 1. Buscar al adoptante autenticado
		Usuario adoptante = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario: " + username));

		// 2. Buscar la solicitud
		SolicitudAdopcion solicitud = solicitudRepository.findById(idSolicitud).orElseThrow(
				() -> new RecursoNoEncontradoException("No se encontró la solicitud con ID: " + idSolicitud));

		// 3. Verificar que la solicitud pertenezca al adoptante
		if (!solicitud.getAdoptante().getIdUsuario().equals(adoptante.getIdUsuario())) {

			throw new RecursoNoEncontradoException("No se encontró la solicitud con ID: " + idSolicitud);
		}

		// 4. Obtener las respuestas de la solicitud
		List<RespuestaSolicitudDetalleDTO> respuestas = respuestaSolicitudRepository
				.findBySolicitud_IdSolicitudOrderByOrdenPreguntaAsc(idSolicitud).stream()
				.map(respuesta -> new RespuestaSolicitudDetalleDTO(respuesta.getPreguntaTextoSnapshot(),
						respuesta.getRespuesta(), respuesta.getOrdenPregunta()))
				.toList();

		// 5. Construir el DTO de detalle
		return new SolicitudAdopcionDetalleDTO(solicitud.getIdSolicitud(), solicitud.getMascota().getIdMascota(),
				solicitud.getMascota().getNombre(), solicitud.getMascota().getTipoMascota().getNombre(),
				solicitud.getMascota().getFotoUrl(), solicitud.getAdoptante().getNombres(),
				solicitud.getAdoptante().getApellidos(), solicitud.getAdoptante().getEmail(),
				solicitud.getFechaSolicitud(), solicitud.getEstado(), solicitud.getFechaResolucion(),
				obtenerNombreResponsable(solicitud), solicitud.getObservacionResolucion(), respuestas);
	}

	@Transactional(readOnly = true)
	public List<SolicitudAdopcionGestorDTO> listarSolicitudesGestor(String username, EstadoSolicitud estado) {

		// 1. Buscar al gestor autenticado
		Usuario gestor = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario: " + username));

		// 2. Obtener las solicitudes de sus propias mascotas
		List<SolicitudAdopcion> solicitudes = solicitudRepository
				.findByMascota_RegistradoPor_IdUsuarioAndEstadoOrderByFechaSolicitudDesc(gestor.getIdUsuario(), estado);

		// 3. Convertir las entidades en DTO
		return solicitudes.stream()
				.map(solicitud -> new SolicitudAdopcionGestorDTO(solicitud.getIdSolicitud(),
						solicitud.getMascota().getIdMascota(), solicitud.getMascota().getNombre(),
						solicitud.getAdoptante().getNombres(), solicitud.getAdoptante().getApellidos(),
						solicitud.getFechaSolicitud(), solicitud.getEstado()))
				.toList();
	}

	@Transactional
	public SolicitudAdopcionRespuestaDTO aprobarSolicitud(Long idSolicitud, ResolverSolicitudDTO datos,
			String username) {

		// 1. Buscar al gestor autenticado
		Usuario gestor = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario: " + username));

		// 2. Obtener la solicitud para conocer su mascota
		SolicitudAdopcion solicitudInicial = solicitudRepository.findById(idSolicitud).orElseThrow(
				() -> new RecursoNoEncontradoException("No se encontró la solicitud con ID: " + idSolicitud));
		Long idMascota = solicitudInicial.getMascota().getIdMascota();

		// 3. Bloquear la mascota para evitar aprobaciones simultáneas
		Mascota mascota = mascotaRepository.buscarPorIdConBloqueo(idMascota)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la mascota con ID: " + idMascota));

		// 4. Verificar que la mascota pertenezca al gestor
		if (!mascota.getRegistradoPor().getIdUsuario().equals(gestor.getIdUsuario())) {
			throw new RecursoNoEncontradoException("No se encontró la solicitud con ID: " + idSolicitud);
		}

		// 5. Volver a buscar y bloquear la solicitud
		SolicitudAdopcion solicitud = solicitudRepository.buscarPorIdConBloqueo(idSolicitud).orElseThrow(
				() -> new RecursoNoEncontradoException("No se encontró la solicitud con ID: " + idSolicitud));

		// 6. Comprobar que siga pendiente
		if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
			throw new DatoInvalidoException("La solicitud ya fue resuelta anteriormente.");
		}

		// 7. Verificar que la mascota pueda ser adoptada
		if (mascota.getEstadoPublicacion() != EstadoPublicacion.ACTIVA
				|| mascota.getEstadoAdopcion() != EstadoAdopcion.DISPONIBLE) {
			throw new DatoInvalidoException("La mascota ya no se encuentra disponible para adopción.");
		}

		// 8. Obtener todas las solicitudes pendientes de la mascota
		List<SolicitudAdopcion> solicitudesPendientes = solicitudRepository.buscarPorMascotaYEstadoConBloqueo(idMascota,
				EstadoSolicitud.PENDIENTE);
		LocalDateTime fechaResolucion = LocalDateTime.now();
		String observacion = datos.getObservacion();
		if (observacion != null) {
			observacion = observacion.trim();
			if (observacion.isEmpty()) {
				observacion = null;
			}
		}
		List<EventoAdopcion> eventos = new ArrayList<>();
		boolean solicitudEncontrada = false;

		// 9. Aprobar la seleccionada y rechazar las demás
		for (SolicitudAdopcion pendiente : solicitudesPendientes) {
			pendiente.setResueltoPor(gestor);
			pendiente.setFechaResolucion(fechaResolucion);
			if (pendiente.getIdSolicitud().equals(idSolicitud)) {

				// Aprobar la solicitud seleccionada
				pendiente.setEstado(EstadoSolicitud.APROBADA);
				pendiente.setObservacionResolucion(observacion);
				solicitudEncontrada = true;
				eventos.add(new EventoAdopcion(pendiente, gestor, TipoEventoAdopcion.SOLICITUD_APROBADA,
						"El gestor aprobó la solicitud de adopción."));
			} else {
				// Rechazar automáticamente las demás solicitudes
				pendiente.setEstado(EstadoSolicitud.RECHAZADA);
				pendiente.setObservacionResolucion("Rechazada automáticamente porque otra solicitud fue aprobada.");
				eventos.add(new EventoAdopcion(pendiente, gestor, TipoEventoAdopcion.RECHAZO_AUTOMATICO_POR_ADOPCION,
						"Otra solicitud para esta mascota fue aprobada."));
			}
		}
		if (!solicitudEncontrada) {
			throw new DatoInvalidoException("La solicitud ya no se encuentra pendiente.");
		}

		// 10. Marcar la mascota como adoptada
		mascota.setEstadoAdopcion(EstadoAdopcion.ADOPTADA);

		// 11. Guardar los cambios
		solicitudRepository.saveAll(solicitudesPendientes);
		mascotaRepository.save(mascota);
		eventoRepository.saveAll(eventos);

		// 12. Devolver la respuesta
		return new SolicitudAdopcionRespuestaDTO(solicitud.getIdSolicitud(), mascota.getIdMascota(),
				mascota.getNombre(), mascota.getTipoMascota().getNombre(), solicitud.getFechaSolicitud(),
				solicitud.getEstado());
	}

	@Transactional
	public SolicitudAdopcionRespuestaDTO rechazarSolicitud(Long idSolicitud, ResolverSolicitudDTO datos,
			String username) {

		// 1. Buscar al gestor autenticado
		Usuario gestor = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario: " + username));

		// 2. Buscar y bloquear la solicitud
		SolicitudAdopcion solicitud = solicitudRepository.buscarPorIdConBloqueo(idSolicitud).orElseThrow(
				() -> new RecursoNoEncontradoException("No se encontró la solicitud con ID: " + idSolicitud));

		// 3. Verificar que la mascota pertenezca al gestor
		if (!solicitud.getMascota().getRegistradoPor().getIdUsuario().equals(gestor.getIdUsuario())) {
			throw new RecursoNoEncontradoException("No se encontró la solicitud con ID: " + idSolicitud);
		}

		// 4. Comprobar que la solicitud siga pendiente
		if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
			throw new DatoInvalidoException("La solicitud ya fue resuelta anteriormente.");
		}

		// 5. Preparar la resolución
		String observacion = datos.getObservacion();
		if (observacion != null) {
			observacion = observacion.trim();
			if (observacion.isEmpty()) {
				observacion = null;
			}
		}
		solicitud.setEstado(EstadoSolicitud.RECHAZADA);
		solicitud.setResueltoPor(gestor);
		solicitud.setFechaResolucion(LocalDateTime.now());
		solicitud.setObservacionResolucion(observacion);

		// 6. Guardar la solicitud
		solicitudRepository.save(solicitud);

		// 7. Registrar el evento histórico
		EventoAdopcion evento = new EventoAdopcion(solicitud, gestor, TipoEventoAdopcion.SOLICITUD_RECHAZADA,
				observacion != null ? observacion : "El gestor rechazó la solicitud de adopción.");
		eventoRepository.save(evento);

		// 8. Devolver la respuesta
		Mascota mascota = solicitud.getMascota();
		return new SolicitudAdopcionRespuestaDTO(solicitud.getIdSolicitud(), mascota.getIdMascota(),
				mascota.getNombre(), mascota.getTipoMascota().getNombre(), solicitud.getFechaSolicitud(),
				solicitud.getEstado());
	}

	@Transactional(readOnly = true)
	public SolicitudAdopcionDetalleDTO obtenerDetalleSolicitudGestor(Long idSolicitud, String username) {

		// 1. Buscar al gestor autenticado
		Usuario gestor = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario: " + username));

		// 2. Buscar la solicitud
		SolicitudAdopcion solicitud = solicitudRepository.findById(idSolicitud).orElseThrow(
				() -> new RecursoNoEncontradoException("No se encontró la solicitud con ID: " + idSolicitud));

		// 3. Verificar que la mascota pertenezca al gestor
		if (!solicitud.getMascota().getRegistradoPor().getIdUsuario().equals(gestor.getIdUsuario())) {
			throw new RecursoNoEncontradoException("No se encontró la solicitud con ID: " + idSolicitud);
		}

		// 4. Obtener las respuestas
		List<RespuestaSolicitudDetalleDTO> respuestas = respuestaSolicitudRepository
				.findBySolicitud_IdSolicitudOrderByOrdenPreguntaAsc(idSolicitud).stream()
				.map(respuesta -> new RespuestaSolicitudDetalleDTO(respuesta.getPreguntaTextoSnapshot(),
						respuesta.getRespuesta(), respuesta.getOrdenPregunta()))
				.toList();

		// 5. Construir el detalle
		return new SolicitudAdopcionDetalleDTO(solicitud.getIdSolicitud(), solicitud.getMascota().getIdMascota(),
				solicitud.getMascota().getNombre(), solicitud.getMascota().getTipoMascota().getNombre(),
				solicitud.getMascota().getFotoUrl(), solicitud.getAdoptante().getNombres(),
				solicitud.getAdoptante().getApellidos(), solicitud.getAdoptante().getEmail(),
				solicitud.getFechaSolicitud(), solicitud.getEstado(), solicitud.getFechaResolucion(),
				obtenerNombreResponsable(solicitud), solicitud.getObservacionResolucion(), respuestas);
	}

	@Transactional(readOnly = true)
	public List<EventoAdopcionDTO> obtenerHistorialAdoptante(Long idSolicitud, String username) {

		// 1. Buscar al adoptante autenticado
		Usuario adoptante = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario: " + username));

		// 2. Buscar la solicitud
		SolicitudAdopcion solicitud = solicitudRepository.findById(idSolicitud).orElseThrow(
				() -> new RecursoNoEncontradoException("No se encontró la solicitud con ID: " + idSolicitud));

		// 3. Verificar que pertenezca al adoptante
		if (!solicitud.getAdoptante().getIdUsuario().equals(adoptante.getIdUsuario())) {
			throw new RecursoNoEncontradoException("No se encontró la solicitud con ID: " + idSolicitud);
		}

		// 4. Obtener el historial
		List<EventoAdopcion> eventos = eventoRepository.findBySolicitud_IdSolicitudOrderByFechaEventoAsc(idSolicitud);
		return convertirEventos(eventos);
	}

	@Transactional(readOnly = true)
	public List<EventoAdopcionDTO> obtenerHistorialGestor(Long idSolicitud, String username) {

		// 1. Buscar al gestor autenticado
		Usuario gestor = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario: " + username));

		// 2. Buscar la solicitud
		SolicitudAdopcion solicitud = solicitudRepository.findById(idSolicitud).orElseThrow(
				() -> new RecursoNoEncontradoException("No se encontró la solicitud con ID: " + idSolicitud));

		// 3. Verificar que la mascota pertenezca al gestor
		if (!solicitud.getMascota().getRegistradoPor().getIdUsuario().equals(gestor.getIdUsuario())) {
			throw new RecursoNoEncontradoException("No se encontró la solicitud con ID: " + idSolicitud);
		}

		// 4. Obtener el historial
		List<EventoAdopcion> eventos = eventoRepository.findBySolicitud_IdSolicitudOrderByFechaEventoAsc(idSolicitud);
		return convertirEventos(eventos);
	}

	@Transactional(readOnly = true)
	public Page<EventoAdopcionDTO> listarHistorialGeneral(Pageable pageable) {

		// 1. Obtener los eventos paginados
		Page<EventoAdopcion> eventos = eventoRepository.findAllByOrderByFechaEventoDesc(pageable);

		// 2. Convertir las entidades en DTO
		return eventos.map(this::convertirEvento);
	}

	@Transactional(readOnly = true)
	public Page<EventoAdopcionDTO> listarHistorialGeneral(TipoEventoAdopcion tipoEvento, Pageable pageable) {

		// 1. Consultar todos los eventos o filtrar por tipo
		Page<EventoAdopcion> eventos;
		if (tipoEvento == null) {
			eventos = eventoRepository.findAllByOrderByFechaEventoDesc(pageable);
		} else {
			eventos = eventoRepository.findByTipoEventoOrderByFechaEventoDesc(tipoEvento, pageable);
		}

		// 2. Convertir las entidades en DTO
		return eventos.map(this::convertirEvento);
	}

	@Transactional(readOnly = true)
	public List<PreguntaSolicitudDTO> listarPreguntasActivas() {

		// 1. Obtener las preguntas activas ordenadas
		List<PreguntaSolicitud> preguntas = preguntaSolicitudRepository.findByActivaTrueOrderByOrdenAsc();

		// 2. Convertir las entidades en DTO
		return preguntas.stream()
				.map(pregunta -> new PreguntaSolicitudDTO(pregunta.getIdPregunta(), pregunta.getTexto(),
						pregunta.getTipoRespuesta(), pregunta.getOpciones(), pregunta.getOrden(),
						pregunta.isObligatoria()))
				.toList();
	}

	private void validarFormatoRespuesta(PreguntaSolicitud pregunta, String respuesta) {
		String valor = respuesta.trim();
		switch (pregunta.getTipoRespuesta()) {
		case TEXTO -> {
			// Cualquier texto no vacío es válido.
		}
		case SI_NO -> {
			String normalizado = valor.toUpperCase(Locale.ROOT).replace("Í", "I");
			if (!normalizado.equals("SI") && !normalizado.equals("NO")) {
				throw new DatoInvalidoException("La respuesta debe ser SI o NO.");
			}
		}
		case NUMERO -> {
			try {
				new BigDecimal(valor);
			} catch (NumberFormatException e) {
				throw new DatoInvalidoException("La respuesta debe ser un número válido.");
			}
		}
		case SELECCION -> {
			JsonNode opciones = pregunta.getOpciones();
			if (opciones == null || !opciones.isArray()) {
				throw new DatoInvalidoException("La pregunta no tiene opciones configuradas.");
			}
			boolean opcionValida = false;
			for (JsonNode opcion : opciones) {
				if (opcion.isString() && opcion.asString().equals(valor)) {
					opcionValida = true;
					break;
				}
			}
			if (!opcionValida) {
				throw new DatoInvalidoException("La respuesta seleccionada no es válida.");
			}
		}
		}
	}

	private List<EventoAdopcionDTO> convertirEventos(List<EventoAdopcion> eventos) {
		return eventos.stream().map(this::convertirEvento).toList();
	}

	private EventoAdopcionDTO convertirEvento(EventoAdopcion evento) {
		Usuario actor = evento.getActor();
		String nombreActor;
		if (actor == null) {
			nombreActor = "Usuario no disponible";
		} else {
			nombreActor = actor.getNombres();
			if (actor.getApellidos() != null && !actor.getApellidos().isBlank()) {
				nombreActor += " " + actor.getApellidos();
			}
		}
		return new EventoAdopcionDTO(evento.getIdEvento(), evento.getTipoEvento(), evento.getDetalle(), nombreActor,
				evento.getFechaEvento());
	}

	private String obtenerNombreResponsable(SolicitudAdopcion solicitud) {
		Usuario responsable = solicitud.getResueltoPor();

		if (responsable == null) {
			return null;
		}
		String nombre = responsable.getNombres();

		if (responsable.getApellidos() != null && !responsable.getApellidos().isBlank()) {
			nombre += " " + responsable.getApellidos();
		}

		return nombre;
	}

}