package br.com.serralheriagarcia.controller;

import br.com.serralheriagarcia.domain.Material;
import br.com.serralheriagarcia.dto.MaterialRequest;
import br.com.serralheriagarcia.dto.Mensagem;
import br.com.serralheriagarcia.service.MaterialService;
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
@RequestMapping("/api/materiais")
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    @GetMapping
    public List<Material> listar() {
        return materialService.listar();
    }

    @GetMapping("/{id}")
    public Material buscar(@PathVariable("id") Integer id) {
        return materialService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mensagem inserir(@Valid @RequestBody MaterialRequest dados) {
        materialService.inserir(dados);
        return new Mensagem("Material inserido!");
    }

    @PutMapping("/{id}")
    public Mensagem alterar(@PathVariable("id") Integer id, @Valid @RequestBody MaterialRequest dados) {
        materialService.alterar(id, dados);
        return new Mensagem("Material atualizado!");
    }

    @DeleteMapping("/{id}")
    public Mensagem remover(@PathVariable("id") Integer id) {
        materialService.remover(id);
        return new Mensagem("Material removido!");
    }
}
