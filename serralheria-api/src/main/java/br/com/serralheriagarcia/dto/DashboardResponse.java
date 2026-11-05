package br.com.serralheriagarcia.dto;

import br.com.serralheriagarcia.domain.Material;
import br.com.serralheriagarcia.domain.Pedido;
import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        long totalClientes,
        long totalPedidos,
        long pedidosPendentes,
        long pedidosPagos,
        long pedidosAtrasados,
        BigDecimal valorRecebido,
        BigDecimal valorEmAberto,
        List<Material> estoqueBaixo,
        List<Pedido> ultimosPedidos) {
}
