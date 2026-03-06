package br.com.forum_hub.services;


import br.com.forum_hub.domain.curso.Curso;
import br.com.forum_hub.domain.curso.CursoService;
import br.com.forum_hub.domain.perfil.HierarquiaService;
import br.com.forum_hub.domain.perfil.Perfil;
import br.com.forum_hub.domain.topico.*;
import br.com.forum_hub.domain.usuario.DadosCadastroUsuario;
import br.com.forum_hub.domain.usuario.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicoServiceTest {

    @InjectMocks
    private TopicoService topicoService;

    @Mock
    private TopicoRepository repository;

    @Mock
    private CursoService cursoService;

    @Mock
    private HierarquiaService hierarquiaService;

    @Mock
    private Topico topico;

    @Nested
    class createTopico{
        @Test
        @DisplayName("Shouldn't register without an author")
        void naoDeveriaCadastrarTopicoSemUsuario() {
            DadosCadastroTopico dto = new DadosCadastroTopico(
                    "Titulo teste",
                    "Mensagem teste",
                    (Long) 1L
            );
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                topicoService.cadastrar(dto, null);
            });
        }


        @Test
        @DisplayName("Should create Topico with Success")
        void deveriaCriarUmTopicoComSucesso(){
            //arrange
            DadosCadastroTopico dto = new DadosCadastroTopico(
                    "Titulo teste",
                    "Mensagem teste",
                    (Long) 1L
            );

            DadosCadastroUsuario dadosUsuario = new DadosCadastroUsuario("teste", "$2a$12$KqncT4ycv5n/BpFAJEmsMO2/yFAN.e5h2crBvrf73qfTZx6Clxn1K", "teste", "teste", "", "");
            var perfil = new Perfil();
            var user = new Usuario(dadosUsuario, dadosUsuario.senha(), perfil);

            when(cursoService.buscarPeloId(dto.cursoId())).thenReturn(new Curso());
            when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            Topico output = topicoService.cadastrar(dto, user);

            Assertions.assertNotNull(output);
        }
    }

    @Nested
    class deleteTopico{
        @Test
        @DisplayName("user role is not allowed delete topic")
        void naoDeveriaExcluirTopicoSemPermissao() {

            Usuario logado = new Usuario();
            Usuario autor = new Usuario();

            when(topico.getAutor()).thenReturn(autor);
            when(repository.findById(1L)).thenReturn(Optional.of(topico));

            when(hierarquiaService.usuarioNaoTemPermissoes(logado, autor, "ROLE_MODERADOR"))
                    .thenReturn(true);

            Assertions.assertThrows(AccessDeniedException.class, () -> {
                topicoService.excluir(1L, logado);
            });

            verify(repository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Should delete topic")
        void deveriaExcluirTopico(){

            Long id = 1L;
            Usuario logado = new Usuario();
            Usuario autor = new Usuario();

            when(topico.getAutor()).thenReturn(autor);
            when(topico.getStatus()).thenReturn(Status.NAO_RESPONDIDO);

            when(repository.findById(id)).thenReturn(Optional.of(topico));
            when(hierarquiaService.usuarioNaoTemPermissoes(logado,autor, "ROLE_MODERADOR")).thenReturn(false);

            topicoService.excluir(id, logado);

            verify(repository).deleteById(id);
        }

    }

    @Nested
    class updateTopico {
        @Test
        void naoDeveriaAtualizarTopicoSemPermissao() {

            Usuario logado = new Usuario();
            Usuario autor = new Usuario();

            DadosAtualizacaoTopico dados =
                    new DadosAtualizacaoTopico(1L, "Titulo", "Mensagem", 1L);

            when(repository.findById(1L)).thenReturn(Optional.of(topico));
            when(topico.getAutor()).thenReturn(autor);

            when(hierarquiaService.usuarioNaoTemPermissoes(logado, autor, "ROLE_MODERADOR"))
                    .thenReturn(true);

            Assertions.assertThrows(AccessDeniedException.class, () -> {
                topicoService.atualizar(dados, logado);
            });

            verify(cursoService, never()).buscarPeloId(any());
        }
    }


}