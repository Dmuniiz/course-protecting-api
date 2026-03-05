package br.com.forum_hub.domain.auth;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    public ResponseCookie generateAccessTokenCookie(String token) {
        return createCookie("accessToken", token, 900); // 15 min
    }

    public ResponseCookie generateRefreshTokenCookie(String token) {
        return createCookie("refreshToken", token, 86400); // 24h
    }

    private ResponseCookie createCookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true) // Mudar para true em produção
                .path("/")
                .maxAge(maxAge)
                .sameSite("Strict")
                .build();
    }

}
