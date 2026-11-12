package com.lovePaws.security;

import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;

import com.lovePaws.usuario.entity.EstadoUsuario;
import com.lovePaws.usuario.entity.RolNombre;
import com.lovePaws.usuario.repository.UsuarioRepository;

@Configuration
public class JwtConfig {

	// Obtener la clave secreta desde la variable de entorno
	@Bean
	public SecretKey jwtSecretKey(@Value("${LOVEPAWS_JWT_SECRET}") String secret) {

		byte[] keyBytes;

		try {
			keyBytes = Base64.getDecoder().decode(secret);
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("La clave secreta JWT no tiene un formato Base64 válido.");
		}

		// Verificar que tenga al menos 256 bits
		if (keyBytes.length < 32) {
			throw new IllegalStateException("La clave secreta JWT debe tener al menos 32 bytes.");
		}

		return new SecretKeySpec(keyBytes, "HmacSHA256");
	}

	// Componente encargado de generar los JWT
	@Bean
	public JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
		return NimbusJwtEncoder.withSecretKey(jwtSecretKey).algorithm(MacAlgorithm.HS256).build();
	}

	// Componente encargado de validar los JWT
	@Bean
	public JwtDecoder jwtDecoder(SecretKey jwtSecretKey, UsuarioRepository usuarioRepository) {
		NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtSecretKey).macAlgorithm(MacAlgorithm.HS256)
				.build();

		// Validar que la cuenta siga activa y que la versión del token coincida
		OAuth2TokenValidator<Jwt> cuentaActivaValidator = jwt -> {

			Object versionClaim = jwt.getClaims().get("tokenVersion");

			if (!(versionClaim instanceof Number version)) {
				OAuth2Error error = new OAuth2Error("invalid_token", "El token no contiene una versión válida.", null);

				return OAuth2TokenValidatorResult.failure(error);
			}

			boolean cuentaValida = usuarioRepository.existsByUsernameAndEstadoAndRol_NombreAndTokenVersion(
					jwt.getSubject(), EstadoUsuario.ACTIVO, RolNombre.ADOPTANTE, version.longValue());

			if (cuentaValida) {
				return OAuth2TokenValidatorResult.success();
			}

			OAuth2Error error = new OAuth2Error("invalid_token", "La cuenta no está activa o el token ha sido revocado.", null);

			return OAuth2TokenValidatorResult.failure(error);
		};

		// Combinar las validaciones estándar con la validación de cuenta
		decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefaultWithIssuer("lovepaws"),
				cuentaActivaValidator));

		return decoder;
	}

	// Convertir los roles del JWT en autoridades de Spring Security
	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();

		authoritiesConverter.setAuthoritiesClaimName("roles");
		authoritiesConverter.setAuthorityPrefix("");

		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

		converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

		return converter;
	}
}
