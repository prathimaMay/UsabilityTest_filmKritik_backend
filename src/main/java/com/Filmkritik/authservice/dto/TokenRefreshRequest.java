package com.Filmkritik.authservice.dto;

public class TokenRefreshRequest {

	private String token;
	private String refreshToken;

	public TokenRefreshRequest(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public TokenRefreshRequest() {
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
