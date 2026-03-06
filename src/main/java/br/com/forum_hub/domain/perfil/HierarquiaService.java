package br.com.forum_hub.domain.perfil;

import br.com.forum_hub.domain.usuario.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HierarquiaService {

    private final RoleHierarchy roleHierarchy;


    public boolean usuarioNaoTemPermissoes(Usuario logado, Usuario autor, String perfilDesejado) {
        return logado.getAuthorities().stream()
                .flatMap(authority -> roleHierarchy.getReachableGrantedAuthorities(List.of(authority)).stream())
                .noneMatch(perfil -> perfil.getAuthority().equals(perfilDesejado) || logado.getId().equals(autor.getId()));
    }

}
