package com.lovePaws.mascota.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lovePaws.mascota.entity.Raza;

public interface RazaRepository extends JpaRepository<Raza, Long> {

	List<Raza> findByTipoMascota_IdTipoMascotaAndActivaTrue(Integer idTipoMascota);

	boolean existsByTipoMascota_IdTipoMascotaAndNombreIgnoreCase(Integer idTipoMascota, String nombre);

	Optional<Raza> findByTipoMascota_IdTipoMascotaAndNombreIgnoreCase(Integer idTipoMascota, String nombre);
}