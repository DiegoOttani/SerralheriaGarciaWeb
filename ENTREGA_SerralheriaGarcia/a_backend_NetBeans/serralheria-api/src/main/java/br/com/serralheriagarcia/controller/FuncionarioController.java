package br.com.serralheriagarcia.controller;

import br.com.serralheriagarcia.config.SomenteAdmin;
import br.com.serralheriagarcia.domain.Funcionario;
import br.com.serralheriagarcia.dto.Mensagem;
import br.com.serralheriagarcia.service.FuncionarioService;
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

// Consulta liberada a todos (usada na tela de pedidos); alterações só para ADMIN
@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    public FuncionarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @GetMapping
    public List<Funcionario> listar() {
        return funcionarioService.listar();
    }

    @GetMapping("/{id}")
    public Funcionario buscar(@PathVariable("id") Integer id) {
        return funcionarioService.buscar(id);
    }

    @SomenteAdmin
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mensagem inserir(@Valid @RequestBody Funcionario funcionario) {
        funcionarioService.inserir(funcionario);
        return new Mensagem("Funcionário inserido!");
    }

    @SomenteAdmin
    @PutMapping("/{id}")
    public Mensagem alterar(@PathVariable("id") Integer id, @Valid @RequestBody Funcionario funcionario) {
        funcionarioService.alterar(id, funcionario);
        return new Mensagem("Funcionário atualizado!");
    }

    @SomenteAdmin
    @DeleteMapping("/{id}")
    public Mensagem remover(@PathVariable("id") Integer id) {
        funcionarioService.remover(id);
        return new Mensagem("Funcionário removido!");
    }
}
