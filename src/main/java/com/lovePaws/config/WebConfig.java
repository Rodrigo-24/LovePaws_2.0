package com.lovePaws.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${app.uploads.mascotas-dir:uploads/mascotas}")
	private String directorioMascotas;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		// 1. Obtener la ruta física del directorio
		Path carpeta = Paths.get(directorioMascotas).toAbsolutePath().normalize();

		// 2. Convertir la ruta a formato URI
		String ubicacion = carpeta.toUri().toString();

		if (!ubicacion.endsWith("/")) {
			ubicacion += "/";
		}

		// 3. Asociar la ruta pública con el directorio físico
		registry.addResourceHandler("/uploads/mascotas/**").addResourceLocations(ubicacion);
	}
}