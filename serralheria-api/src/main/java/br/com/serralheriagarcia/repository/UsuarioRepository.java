package br.com.serralheriagarcia.repository;

import br.com.serralheriagarcia.domain.Perfil;
import br.com.serralheriagarcia.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByLogin(String login);

    List<Usuario> findAllByOrderByNomeAsc();

    boolean existsByLoginAndIdNot(String login, Integer id);

    boolean existsByFuncionarioId(Integer funcionarioId);

    long countByPerfil(Perfil perfil);
}
