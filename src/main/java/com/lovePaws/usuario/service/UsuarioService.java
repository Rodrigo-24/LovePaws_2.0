package com.lovePaws.usuario.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lovePaws.usuario.dto.ActualizarPerfilDTO;
import com.lovePaws.usuario.dto.CambiarPasswordDTO;
import com.lovePaws.usuario.dto.CrearGestorDTO;
import com.lovePaws.usuario.dto.RegistroAdoptanteDTO;
import com.lovePaws.usuario.dto.UsuarioRespuestaDTO;
import com.lovePaws.usuario.entity.EstadoUsuario;
import com.lovePaws.usuario.entity.Rol;
import com.lovePaws.usuario.entity.RolNombre;
import com.lovePaws.usuario.entity.Usuario;
import com.lovePaws.usuario.repository.RolRepository;
import com.lovePaws.usuario.repository.UsuarioRepository;
import com.lovePaws.exception.DatoDuplicadoException;
import com.lovePaws.exception.DatoInvalidoException;
import com.lovePaws.exception.RecursoNoEncontradoException;
import com.lovePaws.solicitud.entity.EstadoSolicitud;
import com.lovePaws.solicitud.entity.EventoAdopcion;
import com.lovePaws.solicitud.entity.SolicitudAdopcion;
import com.lovePaws.solicitud.entity.TipoEventoAdopcion;
import com.lovePaws.solicitud.repository.EventoAdopcionRepository;
import com.lovePaws.solicitud.repository.SolicitudAdopcionRepository;

import jakarta.transaction.Transactional;

@Service
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;
	private final RolRepository rolRepository;
	private final PasswordEncoder passwordEncoder;
	private final SolicitudAdopcionRepository solicitudRepository;
	private final EventoAdopcionRepository eventoRepository;

	public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
			PasswordEncoder passwordEncoder, SolicitudAdopcionRepository solicitudRepository,
			EventoAdopcionRepository eventoRepository) {
		this.usuarioRepository = usuarioRepository;
		this.rolRepository = rolRepository;
		this.passwordEncoder = passwordEncoder;
		this.solicitudRepository = solicitudRepository;
		this.eventoRepository = eventoRepository;
	}

	public List<Usuario> listarUsuarios() {
		return usuarioRepository.findAll();
	}

	public Optional<Usuario> buscarPorId(Long id) {
		return usuarioRepository.findById(id);
	}

	public Optional<Usuario> buscarPorUsername(String username) {
		return usuarioRepository.findByUsername(username);
	}

	public Optional<Usuario> buscarPorEmail(String email) {
		return usuarioRepository.findByEmail(email);
	}

	public List<Usuario> listarPorRol(RolNombre nombre) {
		return usuarioRepository.findAllByRol_Nombre(nombre);
	}

	public List<UsuarioRespuestaDTO> listarUsuariosGestionables(RolNombre rol, EstadoUsuario estado) {

		List<Usuario> usuarios = new ArrayList<>();

		// Si no se especifica un rol, consultar adoptantes y gestores
		if (rol == null) {
			usuarios.addAll(usuarioRepository.findAllByRol_Nombre(RolNombre.ADOPTANTE));
			usuarios.addAll(usuarioRepository.findAllByRol_Nombre(RolNombre.GESTOR));

		} else if (rol == RolNombre.ADOPTANTE || rol == RolNombre.GESTOR) {
			usuarios.addAll(usuarioRepository.findAllByRol_Nombre(rol));

		} else {
			throw new DatoInvalidoException("El rol seleccionado no es válido.");
		}

		// Filtrar por estado y ordenar por fecha de creación
		return usuarios.stream().filter(usuario -> estado == null || usuario.getEstado() == estado)
				.sorted(Comparator.comparing(Usuario::getFechaCreacion).reversed())
				.map(usuario -> new UsuarioRespuestaDTO(usuario.getIdUsuario(), usuario.getUsername(),
						usuario.getEmail(), usuario.getNombres(), usuario.getApellidos(), usuario.getFotoPerfilUrl(),
						usuario.getRol().getNombre(), usuario.getEstado(), usuario.getFechaCreacion()))
				.toList();
	}

	@Transactional
	public UsuarioRespuestaDTO registrarAdoptante(RegistroAdoptanteDTO datos) {
		// COMPROBAR SI EL USERNAME YA EXISTE
		if (usuarioRepository.existsByUsername(datos.getUsername())) {
			throw new DatoDuplicadoException("El nombre de usuario ya está registrado");
		}

		// COMPROBAR SI EL EMAIL YA EXISTE
		if (usuarioRepository.existsByEmail(datos.getEmail())) {
			throw new DatoDuplicadoException("El correo electrónico ya está registrado");
		}

		// BUSCAR EL ROL ADOPTANTE
		Rol rolAdoptante = rolRepository.findByNombre(RolNombre.ADOPTANTE)
				.orElseThrow(() -> new IllegalStateException("No se encontró el rol del ADOPTANTE"));

		// CIFRAR LA CONTRASEÑA
		String passwordHash = passwordEncoder.encode(datos.getPassword());

		// CREAR LA ENTIDAD USUARIO
		Usuario nuevoUsuario = new Usuario(rolAdoptante, datos.getUsername(), datos.getEmail(), passwordHash,
				datos.getNombres(), datos.getApellidos(), EstadoUsuario.ACTIVO);

		// GUARDAR EL USUARIO
		Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

		// CONVERTIR LA ENTIDAD EN DTO DE RESPUESTA
		return new UsuarioRespuestaDTO(usuarioGuardado.getIdUsuario(), usuarioGuardado.getUsername(),
				usuarioGuardado.getEmail(), usuarioGuardado.getNombres(), usuarioGuardado.getApellidos(),
				usuarioGuardado.getFotoPerfilUrl(), usuarioGuardado.getRol().getNombre(), usuarioGuardado.getEstado(),
				usuarioGuardado.getFechaCreacion());
	}

	@Transactional
	public UsuarioRespuestaDTO registrarGestor(CrearGestorDTO datos) {
		// COMPROBAR SI EL USERNAME YA EXISTE
		if (usuarioRepository.existsByUsername(datos.getUsername())) {
			throw new DatoDuplicadoException("El nombre de usuario ya está registrado");
		}

		// COMPROBAR SI EL EMAIL YA EXISTE
		if (usuarioRepository.existsByEmail(datos.getEmail())) {
			throw new DatoDuplicadoException("El correo electrónico ya está registrado");
		}

		// BUSCAR EL ROL GESTOR
		Rol rolGestor = rolRepository.findByNombre(RolNombre.GESTOR)
				.orElseThrow(() -> new IllegalStateException("No se encontró el rol del GESTOR"));

		// CIFRAR LA CONTRASEÑA
		String passwordHash = passwordEncoder.encode(datos.getPassword());

		// CREAR LA ENTIDAD USUARIO
		Usuario nuevoUsuario = new Usuario(rolGestor, datos.getUsername(), datos.getEmail(), passwordHash,
				datos.getNombres(), datos.getApellidos(), EstadoUsuario.ACTIVO);

		// GUARDAR EL USUARIO
		Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

		// CONVERTIR LA ENTIDAD EN DTO DE RESPUESTA
		return new UsuarioRespuestaDTO(usuarioGuardado.getIdUsuario(), usuarioGuardado.getUsername(),
				usuarioGuardado.getEmail(), usuarioGuardado.getNombres(), usuarioGuardado.getApellidos(),
				usuarioGuardado.getFotoPerfilUrl(), usuarioGuardado.getRol().getNombre(), usuarioGuardado.getEstado(),
				usuarioGuardado.getFechaCreacion());
	}

	@Transactional
	public Usuario actualizarCredencialesAdoptante(String usernameAutenticado, ActualizarPerfilDTO datos) {

		// Buscar al usuario autenticado
		Usuario usuario = usuarioRepository.findByUsername(usernameAutenticado)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario."));

		// Verificar que sea adoptante
		if (usuario.getRol().getNombre() != RolNombre.ADOPTANTE) {
			throw new DatoInvalidoException("Solo los adoptantes pueden modificar estas credenciales.");
		}

		// Verificar contraseña actual
		if (!passwordEncoder.matches(datos.getPasswordActual(), usuario.getPasswordHash())) {

			throw new DatoInvalidoException("La contraseña actual es incorrecta.");
		}

		// Normalizar los datos
		String nuevoUsername = datos.getUsername().trim();
		String nuevoEmail = datos.getEmail().trim().toLowerCase(Locale.ROOT);

		// Comprobar nombre de usuario duplicado
		if (usuarioRepository.existsByUsernameAndIdUsuarioNot(nuevoUsername, usuario.getIdUsuario())) {
			throw new DatoDuplicadoException("El nombre de usuario ya está registrado.");
		}
		// Comprobar correo duplicado
		if (usuarioRepository.existsByEmailAndIdUsuarioNot(nuevoEmail, usuario.getIdUsuario())) {
			throw new DatoDuplicadoException("El correo electrónico ya está registrado.");
		}

		// Actualizar credenciales
		usuario.setUsername(nuevoUsername);
		usuario.setEmail(nuevoEmail);

		return usuarioRepository.save(usuario);
	}

	@Transactional
	public void cambiarPassword(String usernameAutenticado, CambiarPasswordDTO datos) {

		// Buscar al usuario autenticado
		Usuario usuario = usuarioRepository.findByUsername(usernameAutenticado)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario."));

		// Verificar que sea adoptante o gestor
		RolNombre rol = usuario.getRol().getNombre();

		if (rol != RolNombre.ADOPTANTE && rol != RolNombre.GESTOR) {
			throw new DatoInvalidoException("No tienes permisos para cambiar esta contraseña.");
		}
		// Verificar contraseña actual
		if (!passwordEncoder.matches(datos.getPasswordActual(), usuario.getPasswordHash())) {
			throw new DatoInvalidoException("La contraseña actual es incorrecta.");
		}
		// Verificar coincidencia de contraseñas
		if (!datos.getPasswordNueva().equals(datos.getConfirmarPassword())) {
			throw new DatoInvalidoException("Las nuevas contraseñas no coinciden.");
		}

		// Evitar reutilizar la contraseña actual
		if (passwordEncoder.matches(datos.getPasswordNueva(), usuario.getPasswordHash())) {
			throw new DatoInvalidoException("La nueva contraseña debe ser diferente a la actual.");
		}

		// Cifrar y guardar nueva contraseña
		usuario.setPasswordHash(passwordEncoder.encode(datos.getPasswordNueva()));
		usuarioRepository.save(usuario);
	}

	@Transactional
	public UsuarioRespuestaDTO bloquearUsuario(Long idUsuario, String usernameAdministrador, String motivo) {

		// 1. Buscar al administrador autenticado
		Usuario administrador = usuarioRepository.findByUsername(usernameAdministrador)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el administrador."));

		// 2. Verificar que quien realiza la operación sea administrador
		if (administrador.getRol().getNombre() != RolNombre.ADMIN) {
			throw new DatoInvalidoException("No tienes permisos para bloquear usuarios.");
		}

		// 3. Buscar al usuario que se desea bloquear
		Usuario usuario = usuarioRepository.findById(idUsuario)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario solicitado."));

		// 4. Impedir el bloqueo de administradores
		RolNombre rol = usuario.getRol().getNombre();

		if (rol == RolNombre.ADMIN) {
			throw new DatoInvalidoException("No se pueden bloquear cuentas de administradores.");
		}

		// 5. Comprobar que la cuenta esté activa
		if (usuario.getEstado() == EstadoUsuario.BLOQUEADO) {
			throw new DatoInvalidoException("La cuenta ya se encuentra bloqueada.");
		}

		// 6. Validar el motivo
		if (motivo == null || motivo.isBlank()) {
			throw new DatoInvalidoException("Debes indicar el motivo del bloqueo.");
		}

		motivo = motivo.trim();

		if (motivo.length() > 500) {
			throw new DatoInvalidoException("El motivo no puede superar los 500 caracteres.");
		}

		// 7. Obtener las solicitudes pendientes relacionadas con el usuario
		List<SolicitudAdopcion> solicitudesPendientes;

		if (rol == RolNombre.ADOPTANTE) {

			solicitudesPendientes = solicitudRepository
					.findByAdoptante_IdUsuarioAndEstadoOrderByFechaSolicitudDesc(idUsuario, EstadoSolicitud.PENDIENTE);

		} else if (rol == RolNombre.GESTOR) {

			solicitudesPendientes = solicitudRepository
					.findByMascota_RegistradoPor_IdUsuarioAndEstadoOrderByFechaSolicitudDesc(idUsuario,
							EstadoSolicitud.PENDIENTE);

		} else {
			throw new DatoInvalidoException("El tipo de usuario no es válido.");
		}

		// 8. Preparar el rechazo automático de las solicitudes
		LocalDateTime fechaBloqueo = LocalDateTime.now();
		List<EventoAdopcion> eventos = new ArrayList<>();

		for (SolicitudAdopcion solicitud : solicitudesPendientes) {

			solicitud.setEstado(EstadoSolicitud.RECHAZADA);
			solicitud.setResueltoPor(administrador);
			solicitud.setFechaResolucion(fechaBloqueo);

			String observacion = rol == RolNombre.ADOPTANTE
					? "Solicitud rechazada automáticamente porque el adoptante fue bloqueado."
					: "Solicitud rechazada automáticamente porque el gestor responsable fue bloqueado.";

			solicitud.setObservacionResolucion(observacion);

			eventos.add(new EventoAdopcion(solicitud, administrador, TipoEventoAdopcion.RECHAZO_AUTOMATICO_POR_BLOQUEO,
					observacion));
		}

		// 9. Guardar los cambios de las solicitudes y su historial
		if (!solicitudesPendientes.isEmpty()) {
			solicitudRepository.saveAll(solicitudesPendientes);
			eventoRepository.saveAll(eventos);
		}

		// 10. Actualizar el estado de la cuenta
		usuario.setEstado(EstadoUsuario.BLOQUEADO);
		usuario.setFechaBloqueo(fechaBloqueo);
		usuario.setBloqueadoPor(administrador);
		usuario.setMotivoBloqueo(motivo);

		Usuario usuarioActualizado = usuarioRepository.save(usuario);

		// 11. Devolver los datos del usuario
		return new UsuarioRespuestaDTO(usuarioActualizado.getIdUsuario(), usuarioActualizado.getUsername(),
				usuarioActualizado.getEmail(), usuarioActualizado.getNombres(), usuarioActualizado.getApellidos(),
				usuarioActualizado.getFotoPerfilUrl(), usuarioActualizado.getRol().getNombre(),
				usuarioActualizado.getEstado(), usuarioActualizado.getFechaCreacion());
	}

	@Transactional
	public UsuarioRespuestaDTO desbloquearUsuario(Long idUsuario, String usernameAdministrador) {

		// 1. Buscar al administrador autenticado
		Usuario administrador = usuarioRepository.findByUsername(usernameAdministrador)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el administrador."));

		// 2. Verificar permisos
		if (administrador.getRol().getNombre() != RolNombre.ADMIN) {
			throw new DatoInvalidoException("No tienes permisos para desbloquear usuarios.");
		}

		// 3. Buscar al usuario que se desea desbloquear
		Usuario usuario = usuarioRepository.findById(idUsuario)
				.orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario solicitado."));

		// 4. Verificar que no sea un administrador
		if (usuario.getRol().getNombre() == RolNombre.ADMIN) {
			throw new DatoInvalidoException("No se pueden gestionar cuentas de administradores.");
		}

		// 5. Comprobar que esté bloqueado
		if (usuario.getEstado() != EstadoUsuario.BLOQUEADO) {
			throw new DatoInvalidoException("La cuenta ya se encuentra activa.");
		}

		// 6. Restaurar el estado de la cuenta
		usuario.setEstado(EstadoUsuario.ACTIVO);
		usuario.setFechaBloqueo(null);
		usuario.setBloqueadoPor(null);
		usuario.setMotivoBloqueo(null);

		Usuario usuarioActualizado = usuarioRepository.save(usuario);

		// 7. Devolver los datos actualizados
		return new UsuarioRespuestaDTO(usuarioActualizado.getIdUsuario(), usuarioActualizado.getUsername(),
				usuarioActualizado.getEmail(), usuarioActualizado.getNombres(), usuarioActualizado.getApellidos(),
				usuarioActualizado.getFotoPerfilUrl(), usuarioActualizado.getRol().getNombre(),
				usuarioActualizado.getEstado(), usuarioActualizado.getFechaCreacion());
	}
}
