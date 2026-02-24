package br.com.forum_hub.domain.usuario;

import br.com.forum_hub.domain.topico.DadosCadastroTopico;
import br.com.forum_hub.domain.topico.Topico;
import br.com.forum_hub.infra.exception.RegraDeNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }


    @Transactional
    public Usuario cadastrar(DadosCadastroUsuario dados) {

        usuarioRepository.findByEmailIgnoreCase(dados.email())
                .ifPresent(u -> {
                    throw new IllegalArgumentException("Já existe uma conta cadastrada com esse email!");
                });

        String encodedPassword = passwordEncoder.encode(dados.senha());
        var user = new Usuario(dados, encodedPassword);

        return usuarioRepository.save(user);
    }

}
