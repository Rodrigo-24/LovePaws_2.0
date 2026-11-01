package com.lovePaws.usuario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovePaws.usuario.entity.EstadoUsuario;
import com.lovePaws.usuario.entity.RolNombre;
import com.lovePaws.usuario.entity.Usuario;
import java.util.List;


public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

	Optional<Usuario> findByUsername(String username);
	Optional<Usuario> findByEmail(String email);
	boolean existsByUsername(String username);
	boolean existsByEmail(String email);
	Optional<Usuario> findByUsernameAndEstado(String username, EstadoUsuario estado);
	
	List<Usuario> findAllByRol_Nombre(RolNombre nombre);
}
