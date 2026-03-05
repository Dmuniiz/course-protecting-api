package br.com.forum_hub.domain.auth;

import br.com.forum_hub.domain.perfil.Perfil;

import java.util.List;

public record UserResponse(
        String nome,
        String email,
        List<Perfil> perfil // Ex: "ROLE_ADMIN"
) {

}