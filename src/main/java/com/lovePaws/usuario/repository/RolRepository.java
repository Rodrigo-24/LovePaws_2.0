package com.lovePaws.usuario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovePaws.usuario.entity.Rol;
import com.lovePaws.usuario.entity.RolNombre;

public interface RolRepository extends JpaRepository<Rol, Byte>{

	Optional<Rol> findByNombre(RolNombre nombre);
}
