package br.com.serralheriagarcia.repository;

import br.com.serralheriagarcia.domain.Funcionario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {

    List<Funcionario> findAllByOrderByNomeAsc();

    boolean existsByCpfAndIdNot(String cpf, Integer id);
}
