package com.lovePaws.mascota.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.lovePaws.mascota.entity.TipoMascota;

public interface TipoMascotaRepository extends JpaRepository<TipoMascota, Integer> {
}