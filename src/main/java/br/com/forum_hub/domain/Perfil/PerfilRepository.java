package br.com.forum_hub.domain.Perfil;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    Perfil findByNome(PerfilNome perfilNome);

}
