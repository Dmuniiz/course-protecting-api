package br.com.forum_hub.domain.usuario;

import br.com.forum_hub.infra.email.EmailService;
import br.com.forum_hub.infra.exception.RegraDeNegocioException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
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

    public Usuario buscarUsuarioPeloNome(String nomeUsuario) {
        return usuarioRepository.findByNomeUsuarioIgnoreCaseAndVerificadoTrueAndAtivoTrue(nomeUsuario)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + nomeUsuario));
    }

    @Transactional
    public Usuario editarPerfil(Usuario logado, @Valid DadosEdicaoUsuario dados) {
        return logado.alterarDados(dados);
    }

    @Transactional
    public void alterarSenha(Usuario logado, @Valid DadosAlterarSenhaUsuario dados) {
        if(!passwordEncoder.matches(dados.senhaAtual(), logado.getPassword())){
            throw new BadCredentialsException("Senha digitada não confere com senha atual!");
        }

        if(!dados.novaSenha().equals(dados.novaSenhaConfirmacao())){
            throw new RegraDeNegocioException("Senha e confirmação não conferem!");
        }

        String encodedPassword = passwordEncoder.encode(dados.novaSenhaConfirmacao());
        logado.alterarSenha(encodedPassword);
    }

    @Transactional
    public void desativarUsuario(Usuario usuario){
        usuario.desativar();
    }
}