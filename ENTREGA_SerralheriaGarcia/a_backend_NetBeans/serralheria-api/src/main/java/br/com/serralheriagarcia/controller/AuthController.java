package br.com.serralheriagarcia.controller;

import br.com.serralheriagarcia.config.AuthInterceptor;
import br.com.serralheriagarcia.dto.LoginRequest;
import br.com.serralheriagarcia.dto.LoginResponse;
import br.com.serralheriagarcia.dto.Mensagem;
import br.com.serralheriagarcia.service.TokenService.Sessao;
import br.com.serralheriagarcia.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Rota pública: recebe login/senha e devolve o token de acesso
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest dados) {
        return usuarioService.login(dados);
    }

    @PostMapping("/logout")
    public Mensagem logout(HttpServletRequest request) {
        usuarioService.logout(AuthInterceptor.extrairToken(request));
        return new Mensagem("Sessão encerrada!");
    }

    // Dados do usuário logado (usado pelo frontend para validar a sessão)
    @GetMapping("/me")
    public Sessao me(@RequestAttribute(AuthInterceptor.ATRIBUTO_SESSAO) Sessao sessao) {
        return sessao;
    }
}
