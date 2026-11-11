package br.com.serralheriagarcia.controller;

import br.com.serralheriagarcia.config.AuthInterceptor;
import br.com.serralheriagarcia.config.SomenteAdmin;
import br.com.serralheriagarcia.domain.Usuario;
import br.com.serralheriagarcia.dto.Mensagem;
import br.com.serralheriagarcia.dto.UsuarioRequest;
import br.com.serralheriagarcia.service.TokenService.Sessao;
import br.com.serralheriagarcia.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// Gestão de usuários: todo o controller é restrito ao perfil ADMIN
@SomenteAdmin
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<Usuario> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    public Usuario buscar(@PathVariable("id") Integer id) {
        return usuarioService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mensagem inserir(@Valid @RequestBody UsuarioRequest dados) {
        usuarioService.inserir(dados);
        return new Mensagem("Usuário inserido!");
    }

    @PutMapping("/{id}")
    public Mensagem alterar(@PathVariable("id") Integer id, @Valid @RequestBody UsuarioRequest dados) {
        usuarioService.alterar(id, dados);
        return new Mensagem("Usuário atualizado!");
    }

    @DeleteMapping("/{id}")
    public Mensagem remover(@PathVariable("id") Integer id,
            @RequestAttribute(AuthInterceptor.ATRIBUTO_SESSAO) Sessao sessao) {
        usuarioService.remover(id, sessao.usuarioId());
        return new Mensagem("Usuário removido!");
    }
}
