package br.com.forum_hub.domain.usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

@Entity
@Table(name="usuarios")
@Getter
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeCompleto;
    private String email;
    private String senha;
    private String nomeUsuario;
    private String biografia;
    private String miniBiografia;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    public Usuario(DadosCadastroUsuario dados, String senhaCriptografada) {
        this.nomeCompleto = dados.nomeCompleto();
        this.email = dados.email();
        this.senha = senhaCriptografada;
        this.nomeUsuario = dados.nomeUsuario();
        this.biografia = dados.biografia();
        this.miniBiografia = dados.miniBiografia();
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

}