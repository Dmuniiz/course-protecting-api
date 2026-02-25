package br.com.forum_hub.domain.usuario;

import br.com.forum_hub.infra.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }

    @Transactional
    public Usuario cadastrar(DadosCadastroUsuario dados) {

        usuarioRepository.findByEmailIgnoreCaseAndVerificadoTrue(dados.email())
                .ifPresent(u -> {
                    throw new IllegalArgumentException("Já existe uma conta cadastrada com esse email!");
                });

        String encodedPassword = passwordEncoder.encode(dados.senha());
        var user = new Usuario(dados, encodedPassword);

        emailService.enviarEmailVerificado(user);
        return usuarioRepository.save(user);
    }

    @Transactional
    public void verificarEmail(String code) {
        var user = usuarioRepository.findByToken(code).orElseThrow();

        user.verificar();
    }
}