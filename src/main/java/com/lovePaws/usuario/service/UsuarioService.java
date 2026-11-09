package com.lovePaws.usuario.service;

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

import jakarta.transaction.Transactional;

@Service
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;
	private final RolRepository rolRepository;
	private final PasswordEncoder passwordEncoder;

	public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
			PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.rolRepository = rolRepository;
		this.passwordEncoder = passwordEncoder;
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
}
