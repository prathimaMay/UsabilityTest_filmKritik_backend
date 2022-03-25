package com.Filmkritik.authservice.dto;

import java.io.Serializable;

public class JwtTokenResponse implements Serializable {

	private static final long serialVersionUID = 8317676219297719109L;

	private final String token;
	private final String role;
	private final String refreshToken;
	private final long jwtExpirationInMs;

	public long getJwtExpirationInMs() {
		return jwtExpirationInMs;
	}

	public String getRole() {
		return role;
	}

	public JwtTokenResponse(String token, String refreshToken, String role, long jwtExpirationInMs) {
		this.token = token;
		this.role = role;
		this.refreshToken = refreshToken;
		this.jwtExpirationInMs = jwtExpirationInMs;
	}

	public String getToken() {
		return this.token;
	}

	public String getRefreshToken() {
		return refreshToken;
	}
}