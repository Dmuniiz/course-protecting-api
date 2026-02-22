package br.com.forum_hub.domain.auth;

import br.com.forum_hub.domain.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService UsuarioService;

    public Authentication authenticate(String email, String senha){
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, senha));
    }


}
