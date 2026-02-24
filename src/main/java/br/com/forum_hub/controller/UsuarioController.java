package br.com.forum_hub.controller;

import br.com.forum_hub.domain.topico.DadosCadastroTopico;
import br.com.forum_hub.domain.topico.DadosListagemTopico;
import br.com.forum_hub.domain.usuario.DadosCadastroUsuario;
import br.com.forum_hub.domain.usuario.DadosListagemUsuario;
import br.com.forum_hub.domain.usuario.Usuario;
import br.com.forum_hub.domain.usuario.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/user")
    public ResponseEntity<DadosListagemUsuario> cadastrar(@RequestBody @Valid DadosCadastroUsuario dados, UriComponentsBuilder uriBuilder) {
        var user = usuarioService.cadastrar(dados);
        var uri = uriBuilder.path("/{nomeUsuario}").buildAndExpand(user.getNomeUsuario()).toUri();
        return ResponseEntity.created(uri).body(new DadosListagemUsuario(user));
    }

}
