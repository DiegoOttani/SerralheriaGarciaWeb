package br.com.serralheriagarcia.service;

import br.com.serralheriagarcia.domain.Perfil;
import br.com.serralheriagarcia.domain.Usuario;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

// Controle das sessões de login: cada login gera um token aleatório guardado em memória
@Service
public class TokenService {

    private static final Duration VALIDADE = Duration.ofHours(8);

    private final Map<String, Sessao> sessoes = new ConcurrentHashMap<>();

    public record Sessao(Integer usuarioId, String nome, Perfil perfil, Instant expiraEm) {
    }

    public String criar(Usuario usuario) {
        String token = UUID.randomUUID().toString();
        sessoes.put(token, new Sessao(usuario.getId(), usuario.getNome(), usuario.getPerfil(),
                Instant.now().plus(VALIDADE)));
        return token;
    }

    public Optional<Sessao> validar(String token) {
        if (token == null) {
            return Optional.empty();
        }
        Sessao sessao = sessoes.get(token);
        if (sessao == null) {
            return Optional.empty();
        }
        if (sessao.expiraEm().isBefore(Instant.now())) {
            sessoes.remove(token);
            return Optional.empty();
        }
        return Optional.of(sessao);
    }

    public void remover(String token) {
        if (token != null) {
            sessoes.remove(token);
        }
    }

    // Encerra as sessões de um usuário (ex.: quando ele é removido ou muda de perfil)
    public void removerDoUsuario(Integer usuarioId) {
        sessoes.values().removeIf(s -> s.usuarioId().equals(usuarioId));
    }
}
