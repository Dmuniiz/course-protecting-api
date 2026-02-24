package br.com.forum_hub.domain.auth;

import br.com.forum_hub.domain.usuario.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public TokenJwtDTO authenticate(String email, String senha){

        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(email, senha);
        var manager = authenticationManager.authenticate(authenticationToken);

        String tokenJWT = tokenService.generateTokenJWT((Usuario) manager.getPrincipal());
        String refreshToken = tokenService.generateRefreshToken((Usuario) manager.getPrincipal());

        return new TokenJwtDTO(tokenJWT,refreshToken);
    }


}
