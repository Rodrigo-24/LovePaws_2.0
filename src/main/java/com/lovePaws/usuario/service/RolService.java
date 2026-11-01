package com.lovePaws.usuario.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lovePaws.usuario.entity.Rol;
import com.lovePaws.usuario.entity.RolNombre;
import com.lovePaws.usuario.repository.RolRepository;

@Service
public class RolService {

	private final RolRepository rolRepository;

	public RolService(RolRepository rolRepository) {
		this.rolRepository = rolRepository;
	}
	
	
	public List<Rol> listarRoles() {
		return rolRepository.findAll();
	}
	
	public Optional<Rol> buscarPorNombre(RolNombre nombre) {
        return rolRepository.findByNombre(nombre);
    }
	
}
