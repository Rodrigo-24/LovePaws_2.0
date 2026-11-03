package com.lovePaws.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	// Manejar datos duplicados
	@ExceptionHandler(DatoDuplicadoException.class)
	public ResponseEntity<ApiErrorDTO> manejarDatoDuplicado(DatoDuplicadoException ex, HttpServletRequest request) {

		ApiErrorDTO error = new ApiErrorDTO(LocalDateTime.now(), HttpStatus.CONFLICT.value(),
				HttpStatus.CONFLICT.getReasonPhrase(), ex.getMessage(), request.getRequestURI());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}

	// Manejar errores de validacion del DTO
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorDTO> manejarValidaciones(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		String mensajes = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage()).distinct()
				.collect(Collectors.joining("; "));

		ApiErrorDTO error = new ApiErrorDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
				HttpStatus.BAD_REQUEST.getReasonPhrase(), mensajes, request.getRequestURI());
		return ResponseEntity.badRequest().body(error);
	}

	// Manejar JSON incorrectos
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorDTO> manejarJsonIncorrecto(HttpMessageNotReadableException ex,
			HttpServletRequest request) {

		ApiErrorDTO error = new ApiErrorDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
				HttpStatus.BAD_REQUEST.getReasonPhrase(), "El cuerpo de la solicitud no contiene un JSON valido.",
				request.getRequestURI());
		return ResponseEntity.badRequest().body(error);
	}

	// Manejar errores inesperados
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorDTO> manejarErrorGeneral(Exception ex, HttpServletRequest request) {

		logger.error("Error inesperado en el servidor", ex);

		ApiErrorDTO error = new ApiErrorDTO(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
				HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Ocurrio un error interno en el servidor.",
				request.getRequestURI());
		return ResponseEntity.internalServerError().body(error);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiErrorDTO> manejarAccesoDenegado(AccessDeniedException ex, HttpServletRequest request) {

		ApiErrorDTO error = new ApiErrorDTO(LocalDateTime.now(), HttpStatus.FORBIDDEN.value(),
				HttpStatus.FORBIDDEN.getReasonPhrase(), "No tienes permisos para realizar esta operación.",
				request.getRequestURI());

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ApiErrorDTO> manejarRecursoNoEncontrado(NoResourceFoundException ex,
			HttpServletRequest request) {

		ApiErrorDTO error = new ApiErrorDTO(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
				HttpStatus.NOT_FOUND.getReasonPhrase(), "No se encontró el recurso solicitado.",
				request.getRequestURI());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(RecursoNoEncontradoException.class)
	public ResponseEntity<ApiErrorDTO> manejarRecursoNoEncontrado(RecursoNoEncontradoException ex,
			HttpServletRequest request) {

		ApiErrorDTO error = new ApiErrorDTO(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
				HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage(), request.getRequestURI());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(DatoInvalidoException.class)
	public ResponseEntity<ApiErrorDTO> manejarDatoInvalido(DatoInvalidoException ex, HttpServletRequest request) {

		ApiErrorDTO error = new ApiErrorDTO(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
				HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage(), request.getRequestURI());

		return ResponseEntity.badRequest().body(error);
	}
}