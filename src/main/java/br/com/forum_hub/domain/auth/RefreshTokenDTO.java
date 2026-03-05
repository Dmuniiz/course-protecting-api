package br.com.forum_hub.domain.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenDTO(
        @NotBlank String token,
        @NotBlank String refreshToken
) {
}
