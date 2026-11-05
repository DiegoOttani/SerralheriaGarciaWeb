package br.com.serralheriagarcia.service;

import br.com.serralheriagarcia.domain.Item;
import br.com.serralheriagarcia.domain.Material;
import br.com.serralheriagarcia.domain.Pedido;
import br.com.serralheriagarcia.dto.ItemRequest;
import br.com.serralheriagarcia.dto.PedidoRequest;
import br.com.serralheriagarcia.exception.NaoEncontradoException;
import br.com.serralheriagarcia.exception.RegraNegocioException;
import br.com.serralheriagarcia.repository.PedidoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Regras do pedido e de seus itens: valor total calculado e controle de estoque dos materiais
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteService clienteService;
    private final FuncionarioService funcionarioService;
    private final MaterialService materialService;

    public PedidoService(PedidoRepository pedidoRepository, ClienteService clienteService,
            FuncionarioService funcionarioService, MaterialService materialService) {
        this.pedidoRepository = pedidoRepository;
        this.clienteService = clienteService;
        this.funcionarioService = funcionarioService;
        this.materialService = materialService;
    }

    public List<Pedido> listar() {
        return pedidoRepository.findAllByOrderByIdDesc();
    }

    public Pedido buscar(Integer id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Pedido não encontrado!"));
    }

    @Transactional
    public Pedido inserir(PedidoRequest dados) {
        Pedido pedido = new Pedido();
        pedido.setDataSolicitacao(LocalDate.now());
        pedido.setValorTotal(BigDecimal.ZERO);
        preencher(pedido, dados);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido alterar(Integer id, PedidoRequest dados) {
        Pedido pedido = buscar(id);
        preencher(pedido, dados);
        return pedidoRepository.save(pedido);
    }

    // Remove o pedido e seus itens, devolvendo os materiais ao estoque
    @Transactional
    public void remover(Integer id) {
        Pedido pedido = buscar(id);
        for (Item item : pedido.getItens()) {
            devolverEstoque(item);
        }
        pedidoRepository.delete(pedido);
    }

    // ---------------- Itens do pedido ----------------

    @Transactional
    public Pedido adicionarItem(Integer pedidoId, ItemRequest dados) {
        Pedido pedido = buscar(pedidoId);
        Item item = new Item();
        item.setPedido(pedido);
        preencherItem(item, dados);
        baixarEstoque(item);
        pedido.getItens().add(item);
        recalcularTotal(pedido);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido alterarItem(Integer pedidoId, Integer itemId, ItemRequest dados) {
        Pedido pedido = buscar(pedidoId);
        Item item = buscarItem(pedido, itemId);
        // Devolve o consumo antigo antes de aplicar o novo (o material pode ter mudado)
        devolverEstoque(item);
        preencherItem(item, dados);
        baixarEstoque(item);
        recalcularTotal(pedido);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido removerItem(Integer pedidoId, Integer itemId) {
        Pedido pedido = buscar(pedidoId);
        Item item = buscarItem(pedido, itemId);
        devolverEstoque(item);
        pedido.getItens().remove(item);
        recalcularTotal(pedido);
        return pedidoRepository.save(pedido);
    }

    // ---------------- Auxiliares ----------------

    private void preencher(Pedido pedido, PedidoRequest dados) {
        pedido.setCliente(clienteService.buscar(dados.clienteId()));
        pedido.setFuncionario(funcionarioService.buscar(dados.funcionarioId()));
        pedido.setEstadoPagamento(dados.estadoPagamento());
        pedido.setDescritivo(dados.descritivo() == null ? null : dados.descritivo().trim());
    }

    private void preencherItem(Item item, ItemRequest dados) {
        item.setMaterial(materialService.buscar(dados.materialId()));
        item.setNome(dados.nome().trim());
        item.setQuantidade(dados.quantidade());
        item.setValorUnidade(dados.valorUnidade());
        item.setDescritivo(dados.descritivo().trim());
        item.setQuantidadeMaterial(dados.quantidadeMaterial());
    }

    private Item buscarItem(Pedido pedido, Integer itemId) {
        return pedido.getItens().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NaoEncontradoException("Item não encontrado neste pedido!"));
    }

    private void baixarEstoque(Item item) {
        Material material = item.getMaterial();
        if (material.getQuantidade() < item.getQuantidadeMaterial()) {
            throw new RegraNegocioException("Estoque insuficiente de " + material.getNome()
                    + "! Disponível: " + material.getQuantidade());
        }
        material.setQuantidade(material.getQuantidade() - item.getQuantidadeMaterial());
    }

    private void devolverEstoque(Item item) {
        Material material = item.getMaterial();
        material.setQuantidade(material.getQuantidade() + item.getQuantidadeMaterial());
    }

    // ValorTotal = soma de (quantidade x valor unitário) dos itens
    private void recalcularTotal(Pedido pedido) {
        BigDecimal total = pedido.getItens().stream()
                .map(Item::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        pedido.setValorTotal(total);
    }
}
