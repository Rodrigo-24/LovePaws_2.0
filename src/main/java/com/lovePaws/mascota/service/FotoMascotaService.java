package com.lovePaws.mascota.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lovePaws.exception.DatoInvalidoException;

@Service
public class FotoMascotaService {

	private static final long TAMANO_MAXIMO = 5L * 1024 * 1024;
	private static final String RUTA_PUBLICA = "/uploads/mascotas/";
	private final Path directorio;

	public FotoMascotaService(@Value("${app.uploads.mascotas-dir:uploads/mascotas}") String directorio) {
		this.directorio = Path.of(directorio).toAbsolutePath().normalize();
	}

	// Guardar una fotografía
	public String guardar(MultipartFile archivo) {

		// 1. Comprobar si se recibió un archivo
		if (archivo == null || archivo.isEmpty()) {
			return null;
		}

		// 2. Validar el tamaño
		if (archivo.getSize() > TAMANO_MAXIMO) {
			throw new DatoInvalidoException("La fotografía no puede superar los 5 MB.");
		}

		try {

			// 3. Obtener el contenido del archivo
			byte[] contenido = archivo.getBytes();

			// 4. Identificar el formato real de la imagen
			String extension = detectarExtension(contenido);

			if (extension == null) {
				throw new DatoInvalidoException("Solo se permiten fotografías JPG, PNG o WEBP.");
			}

			// 5. Crear el directorio si no existe
			Files.createDirectories(directorio);

			// 6. Generar un nombre único
			String nombreArchivo = UUID.randomUUID() + extension;
			Path destino = directorio.resolve(nombreArchivo).normalize();

			// 7. Comprobar que el destino esté dentro del directorio
			if (!destino.startsWith(directorio)) {
				throw new DatoInvalidoException("La ruta de almacenamiento no es válida.");
			}

			// 8. Guardar la fotografía
			Files.write(destino, contenido, StandardOpenOption.CREATE_NEW);

			// 9. Devolver la ruta pública
			return RUTA_PUBLICA + nombreArchivo;

		} catch (IOException e) {
			throw new IllegalStateException("No fue posible almacenar la fotografía.", e);
		}
	}

	public void eliminar(String rutaPublica) {

		if (rutaPublica == null || !rutaPublica.startsWith(RUTA_PUBLICA)) {
			return;
		}

		// Obtener el nombre del archivo
		String nombreArchivo = rutaPublica.substring(RUTA_PUBLICA.length());
		if (nombreArchivo.isBlank() || nombreArchivo.contains("/") || nombreArchivo.contains("\\")) {
			return;
		}

		// Construir la ruta física
		Path archivo = directorio.resolve(nombreArchivo).normalize();

		// Verificar que pertenezca al directorio de fotografías
		if (!archivo.startsWith(directorio)) {
			return;
		}
		try {
			// Eliminar el archivo si existe
			Files.deleteIfExists(archivo);
		} catch (IOException e) {
			throw new IllegalStateException("No fue posible eliminar la fotografía.", e);
		}
	}

	// Identificar el formato mediante su firma binaria
	private String detectarExtension(byte[] contenido) {

		// JPG / JPEG
		if (contenido.length >= 3 && (contenido[0] & 0xFF) == 0xFF && (contenido[1] & 0xFF) == 0xD8
				&& (contenido[2] & 0xFF) == 0xFF) {
			return ".jpg";
		}
		// PNG
		if (contenido.length >= 8 && (contenido[0] & 0xFF) == 0x89 && contenido[1] == 0x50 && contenido[2] == 0x4E
				&& contenido[3] == 0x47 && contenido[4] == 0x0D && contenido[5] == 0x0A && contenido[6] == 0x1A
				&& contenido[7] == 0x0A) {
			return ".png";
		}
		// WEBP
		if (contenido.length >= 12 && contenido[0] == 'R' && contenido[1] == 'I' && contenido[2] == 'F'
				&& contenido[3] == 'F' && contenido[8] == 'W' && contenido[9] == 'E' && contenido[10] == 'B'
				&& contenido[11] == 'P') {
			return ".webp";
		}
		return null;
	}
}