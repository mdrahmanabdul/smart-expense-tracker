package com.smartexpense.dtos;

public class AuthDTO {

	// AuthDtos.java
	public record RegisterRequest(String username, String email, String password) {}

	public record LoginRequest(String username, String email, String password) {}
	public record AuthResponse(String token) {}

}
