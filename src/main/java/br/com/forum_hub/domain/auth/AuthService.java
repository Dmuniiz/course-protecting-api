package br.com.forum_hub.domain.auth;

import br.com.forum_hub.domain.usuario.Usuario;
import br.com.forum_hub.domain.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    public TokenJwtDTO authenticate(String email, String senha){
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(email, senha);
        var manager = authenticationManager.authenticate(authenticationToken);

        Usuario user = (Usuario) manager.getPrincipal();

        String tokenJWT = tokenService.generateTokenJWT(user);
        String refreshToken = tokenService.generateRefreshToken(user);
        UserResponse userResponse = new UserResponse(user.getNomeCompleto(), user.getEmail(), user.getPerfis());

        return new TokenJwtDTO(tokenJWT, refreshToken, userResponse);
    }

    public RefreshTokenDTO refreshTokenAccessLogin(RefreshTokenDTO dto){

        var email = tokenService.getSubjectUser(dto.refreshToken());
        var usuario = usuarioRepository.findByEmailIgnoreCaseAndVerificadoTrue(email).orElseThrow();

        String tokenAccess = tokenService.generateTokenJWT(usuario);
        String updateToken = tokenService.generateRefreshToken(usuario);

        return new RefreshTokenDTO(tokenAccess, updateToken);
    }


}
