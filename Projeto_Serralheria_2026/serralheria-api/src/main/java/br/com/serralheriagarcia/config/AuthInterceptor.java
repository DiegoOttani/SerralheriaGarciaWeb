package br.com.serralheriagarcia.config;

import br.com.serralheriagarcia.domain.Perfil;
import br.com.serralheriagarcia.exception.AcessoNegadoException;
import br.com.serralheriagarcia.exception.NaoAutenticadoException;
import br.com.serralheriagarcia.service.TokenService;
import br.com.serralheriagarcia.service.TokenService.Sessao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

// Executado antes de cada requisição à API: exige um token válido e verifica o perfil
@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String ATRIBUTO_SESSAO = "sessao";

    private final TokenService tokenService;

    public AuthInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Requisições de preflight do CORS (OPTIONS) não chegam a um controller
        if (!(handler instanceof HandlerMethod metodo)) {
            return true;
        }

        Sessao sessao = tokenService.validar(extrairToken(request))
                .orElseThrow(() -> new NaoAutenticadoException("Sessão expirada ou inválida. Faça login novamente."));

        boolean exigeAdmin = metodo.hasMethodAnnotation(SomenteAdmin.class)
                || metodo.getBeanType().isAnnotationPresent(SomenteAdmin.class);
        if (exigeAdmin && sessao.perfil() != Perfil.ADMIN) {
            throw new AcessoNegadoException("Acesso permitido apenas para administradores!");
        }

        request.setAttribute(ATRIBUTO_SESSAO, sessao);
        return true;
    }

    // Header no formato: Authorization: Bearer <token>
    public static String extrairToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
