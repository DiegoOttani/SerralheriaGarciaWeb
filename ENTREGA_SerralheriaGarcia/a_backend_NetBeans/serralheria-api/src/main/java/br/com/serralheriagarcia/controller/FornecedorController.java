package br.com.serralheriagarcia.controller;

import br.com.serralheriagarcia.domain.Fornecedor;
import br.com.serralheriagarcia.dto.Mensagem;
import br.com.serralheriagarcia.service.FornecedorService;
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
@RequestMapping("/api/fornecedores")
public class FornecedorController {

    private final FornecedorService fornecedorService;

    public FornecedorController(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

    @GetMapping
    public List<Fornecedor> listar() {
        return fornecedorService.listar();
    }

    @GetMapping("/{id}")
    public Fornecedor buscar(@PathVariable("id") Integer id) {
        return fornecedorService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mensagem inserir(@Valid @RequestBody Fornecedor fornecedor) {
        fornecedorService.inserir(fornecedor);
        return new Mensagem("Fornecedor inserido!");
    }

    @PutMapping("/{id}")
    public Mensagem alterar(@PathVariable("id") Integer id, @Valid @RequestBody Fornecedor fornecedor) {
        fornecedorService.alterar(id, fornecedor);
        return new Mensagem("Fornecedor atualizado!");
    }

    @DeleteMapping("/{id}")
    public Mensagem remover(@PathVariable("id") Integer id) {
        fornecedorService.remover(id);
        return new Mensagem("Fornecedor removido!");
    }
}
