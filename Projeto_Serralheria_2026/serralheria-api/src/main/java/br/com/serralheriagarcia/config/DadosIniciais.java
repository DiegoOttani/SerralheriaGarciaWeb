package br.com.serralheriagarcia.config;

import br.com.serralheriagarcia.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// Na primeira execução (sem usuários no banco), cria o administrador padrão
@Slf4j
@Component
public class DadosIniciais implements CommandLineRunner {

    private static final String LOGIN_ADMIN = "admin";
    private static final String SENHA_ADMIN = "admin123";

    private final UsuarioService usuarioService;

    public DadosIniciais(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) {
        if (usuarioService.criarAdminInicial(LOGIN_ADMIN, SENHA_ADMIN)) {
            log.info("Usuário administrador criado -> login: {} / senha: {}", LOGIN_ADMIN, SENHA_ADMIN);
        }
    }
}
