package com.lovePaws.usuario.dto;

public class TokenRespuestaDTO {

	private String accessToken;
	private String tokenType;
	private long expiresIn;

	public TokenRespuestaDTO(String accessToken, String tokenType, long expiresIn) {
		this.accessToken = accessToken;
		this.tokenType = tokenType;
		this.expiresIn = expiresIn;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public long getExpiresIn() {
		return expiresIn;
	}
}
