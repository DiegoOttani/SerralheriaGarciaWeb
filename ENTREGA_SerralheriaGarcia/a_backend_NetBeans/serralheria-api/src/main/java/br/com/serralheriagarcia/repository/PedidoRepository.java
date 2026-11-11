package br.com.serralheriagarcia.repository;

import br.com.serralheriagarcia.domain.Pedido;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findAllByOrderByIdDesc();

    boolean existsByClienteId(Integer clienteId);

    boolean existsByFuncionarioId(Integer funcionarioId);

    long countByEstadoPagamento(String estadoPagamento);

    @Query("select coalesce(sum(p.valorTotal), 0) from Pedido p where p.estadoPagamento = ?1")
    BigDecimal somarValorPorEstado(String estadoPagamento);

    List<Pedido> findTop5ByOrderByIdDesc();
}
