package br.com.serralheriagarcia.controller;

import br.com.serralheriagarcia.domain.Cliente;
import br.com.serralheriagarcia.dto.Mensagem;
import br.com.serralheriagarcia.service.ClienteService;
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

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<Cliente> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    public Cliente buscar(@PathVariable("id") Integer id) {
        return clienteService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mensagem inserir(@Valid @RequestBody Cliente cliente) {
        clienteService.inserir(cliente);
        return new Mensagem("Cliente inserido!");
    }

    @PutMapping("/{id}")
    public Mensagem alterar(@PathVariable("id") Integer id, @Valid @RequestBody Cliente cliente) {
        clienteService.alterar(id, cliente);
        return new Mensagem("Cliente atualizado!");
    }

    @DeleteMapping("/{id}")
    public Mensagem remover(@PathVariable("id") Integer id) {
        clienteService.remover(id);
        return new Mensagem("Cliente removido!");
    }
}
