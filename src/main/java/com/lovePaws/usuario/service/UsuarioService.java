package com.lovePaws.usuario.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lovePaws.usuario.dto.RegistroAdoptanteDTO;
import com.lovePaws.usuario.dto.UsuarioRespuestaDTO;
import com.lovePaws.usuario.entity.EstadoUsuario;
import com.lovePaws.usuario.entity.Rol;
import com.lovePaws.usuario.entity.RolNombre;
import com.lovePaws.usuario.entity.Usuario;
import com.lovePaws.usuario.repository.RolRepository;
import com.lovePaws.usuario.repository.UsuarioRepository;
import com.lovePaws.exception.DatoDuplicadoException;

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
	
	public Optional<Usuario> buscarPorUsername(String username){
		return usuarioRepository.findByUsername(username);
	}
	
	public Optional<Usuario> buscarPorEmail(String email){
		return usuarioRepository.findByEmail(email);
	}
	
	public List<Usuario> listarPorRol(RolNombre nombre){
		return usuarioRepository.findAllByRol_Nombre(nombre);
	}
	
	@Transactional
	public UsuarioRespuestaDTO registrarAdoptante(RegistroAdoptanteDTO datos) {
		//COMPROBAR SI EL USERNAME YA EXISTE
		if (usuarioRepository.existsByUsername(datos.getUsername())) {
			throw new DatoDuplicadoException("El nombre de usuario ya está registrado");
		}
		
		//COMPROBAR SI EL EMAIL YA EXISTE
		if (usuarioRepository.existsByEmail(datos.getEmail())) {
			throw new DatoDuplicadoException("El correo electrónico ya está registrado");
		}
		
		//BUSCAR EL ROL ADOPTANTE
		Rol rolAdoptante = rolRepository.findByNombre(RolNombre.ADOPTANTE)
				.orElseThrow(() -> new IllegalStateException("No se encontró el rol del ADOPTANTE")); 
		
		//CIFRAR LA CONTRASEÑA
		String passwordHash = passwordEncoder.encode(datos.getPassword());
		
		//CREAR LA ENTIDAD USUARIO
		Usuario nuevoUsuario = new Usuario(
				rolAdoptante, 
				datos.getUsername(), 
				datos.getEmail(), 
				passwordHash, 
				datos.getNombres(), 
				datos.getApellidos(),
				EstadoUsuario.ACTIVO
		);
		
		//GUARDAR EL USUARIO
		Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);
		
		//CONVERTIR LA ENTIDAD EN DTO DE RESPUESTA
		return new UsuarioRespuestaDTO(
				usuarioGuardado.getIdUsuario(), 
				usuarioGuardado.getUsername(), 
				usuarioGuardado.getEmail(), 
				usuarioGuardado.getNombres(), 
				usuarioGuardado.getApellidos(), 
				usuarioGuardado.getFotoPerfilUrl(), 
				usuarioGuardado.getRol().getNombre(), 
				usuarioGuardado.getEstado(), 
				usuarioGuardado.getFechaCreacion()
		);
	}
}
