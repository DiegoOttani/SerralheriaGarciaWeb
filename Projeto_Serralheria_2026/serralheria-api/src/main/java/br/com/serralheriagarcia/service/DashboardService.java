package br.com.serralheriagarcia.service;

import br.com.serralheriagarcia.dto.DashboardResponse;
import br.com.serralheriagarcia.repository.ClienteRepository;
import br.com.serralheriagarcia.repository.MaterialRepository;
import br.com.serralheriagarcia.repository.PedidoRepository;
import org.springframework.stereotype.Service;

// Resumo exibido na tela inicial
@Service
public class DashboardService {

    // Materiais com estoque igual ou abaixo deste valor aparecem no alerta
    private static final int LIMITE_ESTOQUE_BAIXO = 10;

    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;
    private final MaterialRepository materialRepository;

    public DashboardService(ClienteRepository clienteRepository, PedidoRepository pedidoRepository,
            MaterialRepository materialRepository) {
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
        this.materialRepository = materialRepository;
    }

    public DashboardResponse resumo() {
        return new DashboardResponse(
                clienteRepository.count(),
                pedidoRepository.count(),
                pedidoRepository.countByEstadoPagamento("Pendente"),
                pedidoRepository.countByEstadoPagamento("Pago"),
                pedidoRepository.countByEstadoPagamento("Atrasado"),
                pedidoRepository.somarValorPorEstado("Pago"),
                pedidoRepository.somarValorPorEstado("Pendente").add(pedidoRepository.somarValorPorEstado("Atrasado")),
                materialRepository.findEstoqueBaixo(LIMITE_ESTOQUE_BAIXO),
                pedidoRepository.findTop5ByOrderByIdDesc());
    }
}
