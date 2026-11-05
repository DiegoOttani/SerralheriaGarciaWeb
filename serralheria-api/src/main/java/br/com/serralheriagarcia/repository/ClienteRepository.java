package br.com.serralheriagarcia.repository;

import br.com.serralheriagarcia.domain.Cliente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    List<Cliente> findAllByOrderByNomeAsc();

    boolean existsByCpfAndIdNot(String cpf, Integer id);
}
