package br.com.forum_hub.domain.topico;

import br.com.forum_hub.domain.curso.Categoria;
import br.com.forum_hub.domain.curso.Curso;
import br.com.forum_hub.domain.usuario.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "topicos")
@Getter
public class Topico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String mensagem;


    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Usuario autor;

    private LocalDateTime dataCriacao;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Boolean aberto;
    private Integer quantidadeRespostas;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @Deprecated
    public Topico(){}

    public Topico(DadosCadastroTopico dados, Curso curso, Usuario autor) {
        this.titulo = dados.titulo();
        this.mensagem = dados.mensagem();
        this.autor = autor;
        this.dataCriacao = LocalDateTime.now();
        this.status = Status.NAO_RESPONDIDO;
        this.aberto = Boolean.TRUE;
        this.quantidadeRespostas = getQuantidadeRespostas();
        this.categoria = curso.getCategoria();
        this.curso = curso;
    }

    public Topico atualizarInformacoes(DadosAtualizacaoTopico dados, Curso curso) {
        if(dados.titulo() != null){
            this.titulo = dados.titulo();
        }
        if(dados.mensagem() != null){
            this.mensagem = dados.mensagem();
        }
        if(curso != null){
            this.curso = curso;
        }
        return this;
    }

    public void alterarStatus(Status status) {
        this.status = status;
    }

    public Integer incrementarRespostas() {
        return this.quantidadeRespostas++;
    }

    public void decrementarRespostas() {
        this.quantidadeRespostas--;
    }

    public void fechar() {
        this.aberto = Boolean.FALSE;
    }

    public boolean estaAberto() {
        return this.aberto;
    }
}