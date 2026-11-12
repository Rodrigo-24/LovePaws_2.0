package com.lovePaws.usuario.service;

import java.time.Instant;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;

import com.lovePaws.security.UsuarioDetails;
import com.lovePaws.usuario.dto.LoginAdoptanteDTO;
import com.lovePaws.usuario.dto.TokenRespuestaDTO;

@Service
public class AutenticacionService {

	private static final long EXPIRACION_SEGUNDOS = 3600;

	private final AuthenticationManager authenticationManager;
	private final JwtEncoder jwtEncoder;

	public AutenticacionService(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder) {
		this.authenticationManager = authenticationManager;
		this.jwtEncoder = jwtEncoder;
	}

	// Autenticar al adoptante y generar su token
	public TokenRespuestaDTO autenticarAdoptante(LoginAdoptanteDTO datos) {

		// 1. Comprobar las credenciales
		Authentication autenticacion = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(datos.getUsername(), datos.getPassword()));

		// 2. Comprobar que el usuario tenga el rol ADOPTANTE
		boolean esAdoptante = autenticacion.getAuthorities().stream()
				.anyMatch(autoridad -> autoridad.getAuthority().equals("ROLE_ADOPTANTE"));

		UsuarioDetails usuarioDetails = (UsuarioDetails) autenticacion.getPrincipal();

		if (!esAdoptante) {
			throw new BadCredentialsException("Credenciales inválidas.");
		}

		// 3. Definir las fechas del token
		Instant ahora = Instant.now();
		Instant expiracion = ahora.plusSeconds(EXPIRACION_SEGUNDOS);

		// 4. Construir los datos del JWT
		JwtClaimsSet claims = JwtClaimsSet.builder().issuer("lovepaws").subject(autenticacion.getName()).issuedAt(ahora)
				.expiresAt(expiracion).claim("roles", List.of("ROLE_ADOPTANTE"))
				.claim("tokenVersion", usuarioDetails.getTokenVersion()).build();

		// 5. Firmar el token con HS256
		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

		String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

		// 6. Devolver el token
		return new TokenRespuestaDTO(token, "Bearer", EXPIRACION_SEGUNDOS);
	}
}
