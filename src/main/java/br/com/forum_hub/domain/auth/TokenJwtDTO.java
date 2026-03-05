package br.com.forum_hub.domain.auth;

public record TokenJwtDTO(String token, String refreshToken, UserResponse userResponse) {
}
