package br.com.serralheriagarcia.service;

import br.com.serralheriagarcia.domain.Perfil;
import br.com.serralheriagarcia.domain.Usuario;
import br.com.serralheriagarcia.dto.LoginRequest;
import br.com.serralheriagarcia.dto.LoginResponse;
import br.com.serralheriagarcia.dto.UsuarioRequest;
import br.com.serralheriagarcia.exception.NaoAutenticadoException;
import br.com.serralheriagarcia.exception.NaoEncontradoException;
import br.com.serralheriagarcia.exception.RegraNegocioException;
import br.com.serralheriagarcia.repository.UsuarioRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private static final int TAMANHO_MINIMO_SENHA = 4;

    private final UsuarioRepository usuarioRepository;
    private final FuncionarioService funcionarioService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public UsuarioService(UsuarioRepository usuarioRepository, FuncionarioService funcionarioService,
            PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.funcionarioService = funcionarioService;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    // ---------------- Login ----------------

    public LoginResponse login(LoginRequest dados) {
        Usuario usuario = usuarioRepository.findByLogin(dados.login().trim())
                .filter(u -> passwordEncoder.matches(dados.senha(), u.getSenha()))
                .orElseThrow(() -> new NaoAutenticadoException("Login ou senha inválidos!"));
        String token = tokenService.criar(usuario);
        return new LoginResponse(token, usuario.getId(), usuario.getNome(), usuario.getLogin(), usuario.getPerfil());
    }

    public void logout(String token) {
        tokenService.remover(token);
    }

    // ---------------- CRUD ----------------

    public List<Usuario> listar() {
        return usuarioRepository.findAllByOrderByNomeAsc();
    }

    public Usuario buscar(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Usuário não encontrado!"));
    }

    @Transactional
    public Usuario inserir(UsuarioRequest dados) {
        if (dados.senha() == null || dados.senha().isBlank()) {
            throw new RegraNegocioException("Senha é obrigatória!");
        }
        Usuario usuario = new Usuario();
        preencher(usuario, dados);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario alterar(Integer id, UsuarioRequest dados) {
        Usuario usuario = buscar(id);
        if (usuario.getPerfil() == Perfil.ADMIN && dados.perfil() != Perfil.ADMIN) {
            garantirOutroAdmin();
        }
        preencher(usuario, dados);
        // Força novo login para que o perfil atualizado passe a valer
        tokenService.removerDoUsuario(id);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void remover(Integer id, Integer usuarioLogadoId) {
        Usuario usuario = buscar(id);
        if (id.equals(usuarioLogadoId)) {
            throw new RegraNegocioException("Não é possível remover o próprio usuário!");
        }
        if (usuario.getPerfil() == Perfil.ADMIN) {
            garantirOutroAdmin();
        }
        usuarioRepository.delete(usuario);
        tokenService.removerDoUsuario(id);
    }

    // Cria o administrador inicial quando a tabela de usuários está vazia
    @Transactional
    public boolean criarAdminInicial(String login, String senha) {
        if (usuarioRepository.count() > 0) {
            return false;
        }
        Usuario admin = new Usuario();
        admin.setNome("Administrador");
        admin.setLogin(login);
        admin.setSenha(passwordEncoder.encode(senha));
        admin.setPerfil(Perfil.ADMIN);
        usuarioRepository.save(admin);
        return true;
    }

    private void preencher(Usuario u, UsuarioRequest dados) {
        String login = dados.login().trim();
        Integer id = u.getId() == null ? 0 : u.getId();
        if (usuarioRepository.existsByLoginAndIdNot(login, id)) {
            throw new RegraNegocioException("Já existe um usuário com este login!");
        }
        u.setNome(dados.nome().trim());
        u.setLogin(login);
        u.setPerfil(dados.perfil());
        u.setFuncionario(dados.funcionarioId() == null ? null : funcionarioService.buscar(dados.funcionarioId()));

        if (dados.senha() != null && !dados.senha().isBlank()) {
            if (dados.senha().length() < TAMANHO_MINIMO_SENHA) {
                throw new RegraNegocioException("A senha deve ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres!");
            }
            u.setSenha(passwordEncoder.encode(dados.senha()));
        }
    }

    private void garantirOutroAdmin() {
        if (usuarioRepository.countByPerfil(Perfil.ADMIN) <= 1) {
            throw new RegraNegocioException("O sistema precisa ter pelo menos um administrador!");
        }
    }
}
