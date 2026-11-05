package br.com.serralheriagarcia.repository;

import br.com.serralheriagarcia.domain.Material;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Integer> {

    List<Material> findAllByOrderByNomeAsc();

    boolean existsByFornecedorId(Integer fornecedorId);

    @Query("select m from Material m where m.quantidade <= ?1 order by m.quantidade")
    List<Material> findEstoqueBaixo(Integer limite);
}
