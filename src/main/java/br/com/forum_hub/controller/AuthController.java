package br.com.forum_hub.controller;

import br.com.forum_hub.domain.auth.*;

import br.com.forum_hub.domain.usuario.Usuario;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        var tokens = authService.authenticate(loginRequest.email(), loginRequest.senha());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieService.generateAccessTokenCookie(tokens.token()).toString())
                .header(HttpHeaders.SET_COOKIE, cookieService.generateRefreshTokenCookie(tokens.refreshToken()).toString())
                .body(tokens.userResponse());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> updateToken(HttpServletRequest request){
        Cookie refreshCookie = WebUtils.getCookie(request, "refreshToken");
        Cookie accessToken = WebUtils.getCookie(request, "accessToken");

        if (refreshCookie == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
        }
        // 2. Chama o serviço passando o valor do cookie
        var novosTokens = authService.refreshTokenAccessLogin(new RefreshTokenDTO(refreshCookie.getValue(), accessToken.getValue()));

        // 3. Gera os novos cookies (Roda o Access e o Refresh para segurança máxima)
        ResponseCookie novoAccess = cookieService.generateAccessTokenCookie(novosTokens.token());
        ResponseCookie novoRefresh = cookieService.generateRefreshTokenCookie(novosTokens.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, novoAccess.toString())
                .header(HttpHeaders.SET_COOKIE, novoRefresh.toString())
                .body("Token renovado com sucesso");
    }

}
