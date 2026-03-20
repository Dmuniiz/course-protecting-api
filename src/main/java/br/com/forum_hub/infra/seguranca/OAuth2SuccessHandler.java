package br.com.forum_hub.infra.seguranca;

import br.com.forum_hub.domain.autenticacao.OAuth2UserService;
import br.com.forum_hub.domain.autenticacao.TokenService;
import br.com.forum_hub.domain.usuario.UsuarioRepository;
import br.com.forum_hub.infra.exception.RegraDeNegocioException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {


    private final TokenService tokenService;
    private final UsuarioRepository repository;
    private final OAuth2UserService oAuth2UserService;

    public OAuth2SuccessHandler(TokenService tokenService, UsuarioRepository usuarioRepository, OAuth2UserService oAuth2UserService) {
        this.tokenService = tokenService;
        this.repository = usuarioRepository;
        this.oAuth2UserService = oAuth2UserService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication; //

        String provider = oauth2Token.getAuthorizedClientRegistrationId();

        String email = oAuth2UserService.getEmailFromOAuth2AuthenticationToken(authentication, provider);

        if (email == null) {
            throw new RegraDeNegocioException("Email não encontrado no OAuth2");
        }

        var usuario = repository.findByEmail(email);

        String token = tokenService.gerarToken(usuario);
        String refreshToken = tokenService.gerarRefreshToken(usuario);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"token\": \"" + token + "\"}");
        response.getWriter().write("{\"refreshToken\": \"" + refreshToken + "\"}");
    }


}
