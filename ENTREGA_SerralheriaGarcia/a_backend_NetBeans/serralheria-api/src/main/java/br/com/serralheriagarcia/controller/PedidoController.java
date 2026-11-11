package br.com.serralheriagarcia.controller;

import br.com.serralheriagarcia.domain.Pedido;
import br.com.serralheriagarcia.dto.ItemRequest;
import br.com.serralheriagarcia.dto.Mensagem;
import br.com.serralheriagarcia.dto.PedidoRequest;
import br.com.serralheriagarcia.service.PedidoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// Pedidos e seus itens (Pedido 1:N Item)
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public List<Pedido> listar() {
        return pedidoService.listar();
    }

    @GetMapping("/{id}")
    public Pedido buscar(@PathVariable("id") Integer id) {
        return pedidoService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pedido inserir(@Valid @RequestBody PedidoRequest dados) {
        // Devolve o pedido criado para o frontend já abrir a inclusão de itens
        return pedidoService.inserir(dados);
    }

    @PutMapping("/{id}")
    public Mensagem alterar(@PathVariable("id") Integer id, @Valid @RequestBody PedidoRequest dados) {
        pedidoService.alterar(id, dados);
        return new Mensagem("Pedido atualizado!");
    }

    @DeleteMapping("/{id}")
    public Mensagem remover(@PathVariable("id") Integer id) {
        pedidoService.remover(id);
        return new Mensagem("Pedido removido!");
    }

    // ---------------- Itens ----------------

    @PostMapping("/{id}/itens")
    @ResponseStatus(HttpStatus.CREATED)
    public Mensagem adicionarItem(@PathVariable("id") Integer id, @Valid @RequestBody ItemRequest dados) {
        pedidoService.adicionarItem(id, dados);
        return new Mensagem("Item inserido!");
    }

    @PutMapping("/{id}/itens/{itemId}")
    public Mensagem alterarItem(@PathVariable("id") Integer id, @PathVariable("itemId") Integer itemId,
            @Valid @RequestBody ItemRequest dados) {
        pedidoService.alterarItem(id, itemId, dados);
        return new Mensagem("Item atualizado!");
    }

    @DeleteMapping("/{id}/itens/{itemId}")
    public Mensagem removerItem(@PathVariable("id") Integer id, @PathVariable("itemId") Integer itemId) {
        pedidoService.removerItem(id, itemId);
        return new Mensagem("Item removido!");
    }
}
