package br.com.forum_hub.controller;

import br.com.forum_hub.domain.auth.AuthService;
import br.com.forum_hub.domain.auth.LoginRequest;

import br.com.forum_hub.domain.auth.RefreshTokenDTO;
import br.com.forum_hub.domain.auth.TokenJwtDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenJwtDTO> login(@Valid @RequestBody LoginRequest loginRequest){
        var userAuthentication  = authService.authenticate(loginRequest.email(), loginRequest.senha());

        return ResponseEntity.ok(userAuthentication);
    }

}
