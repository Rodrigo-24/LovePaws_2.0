package com.lovePaws.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.lovePaws.usuario.entity.EstadoUsuario;
import com.lovePaws.usuario.entity.Rol;
import com.lovePaws.usuario.entity.RolNombre;
import com.lovePaws.usuario.entity.Usuario;
import com.lovePaws.usuario.repository.RolRepository;
import com.lovePaws.usuario.repository.UsuarioRepository;

@Component
@ConditionalOnProperty(prefix = "app.bootstrap-admin", name = "enabled", havingValue = "true")
public class AdminInitializer implements ApplicationRunner{

	private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    private final String username;
    private final String email;
    private final String password;
    private final String nombres;
    private final String apellidos;
    
	public AdminInitializer(UsuarioRepository usuarioRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder,
			@Value("${app.bootstrap-admin.username:}") String username,
			@Value("${app.bootstrap-admin.email:}") String email,
			@Value("${app.bootstrap-admin.password:}") String password,
			@Value("${app.bootstrap-admin.nombres:}") String nombres,
			@Value("${app.bootstrap-admin.apellidos:}") String apellidos
			) {
		this.usuarioRepository = usuarioRepository;
		this.rolRepository = rolRepository;
		this.passwordEncoder = passwordEncoder;
		this.username = username;
		this.email = email;
		this.password = password;
		this.nombres = nombres;
		this.apellidos = apellidos;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) throws Exception {
		
		//COMPROBAR SI YA EXISTE UN ADMINISTRADOR
        if (usuarioRepository.existsByRol_Nombre(RolNombre.ADMIN)) {
            logger.info("Ya existe un administrador. No se creará otro.");
            return;
        }
        
        //COMPROBAR SI LAS VARIABLES ESTAN CONFIGURADAS
        if (username.isBlank() || email.isBlank() || password.isBlank() || nombres.isBlank()) {
            throw new IllegalStateException("Faltan datos para crear el administrador inicial.");
        }
		
        //COMPROBAR QUE USERNAME Y EMAIL ESTEN DISPONIBLES
        if (usuarioRepository.existsByUsername(username)) { 
            throw new IllegalStateException("El username del administrador ya está registrado.");
        }
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalStateException("El correo del administrador ya está registrado.");
        }

        //OBTENER EL ROL ADMIN
        Rol rolAdmin = rolRepository.findByNombre(RolNombre.ADMIN)
            .orElseThrow(() -> new IllegalStateException("No se encontró el rol ADMIN en la base de datos."));

        //CIFRAR LA CONTRASEÑA
        String passwordHash = passwordEncoder.encode(password);

        //CREAR LA ENTIDAD USUARIO
        Usuario administrador = new Usuario(
            rolAdmin,
            username,
            email,
            passwordHash,
            nombres,
            apellidos,
            EstadoUsuario.ACTIVO
        );

        //GUARDAR EL ADMINISTRADOR
        usuarioRepository.save(administrador);

        logger.info("Administrador inicial creado correctamente.");
    }  
}
