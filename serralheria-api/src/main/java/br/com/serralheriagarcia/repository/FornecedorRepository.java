package br.com.serralheriagarcia.repository;

import br.com.serralheriagarcia.domain.Fornecedor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Integer> {

    List<Fornecedor> findAllByOrderByNomeAsc();

    boolean existsByCnpjAndIdNot(String cnpj, Integer id);
}
