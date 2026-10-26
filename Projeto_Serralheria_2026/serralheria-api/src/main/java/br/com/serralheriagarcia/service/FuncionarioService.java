package br.com.serralheriagarcia.service;

import br.com.serralheriagarcia.domain.Funcionario;
import br.com.serralheriagarcia.exception.NaoEncontradoException;
import br.com.serralheriagarcia.exception.RegraNegocioException;
import br.com.serralheriagarcia.repository.FuncionarioRepository;
import br.com.serralheriagarcia.repository.PedidoRepository;
import br.com.serralheriagarcia.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    public FuncionarioService(FuncionarioRepository funcionarioRepository, PedidoRepository pedidoRepository,
            UsuarioRepository usuarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Funcionario> listar() {
        return funcionarioRepository.findAllByOrderByNomeAsc();
    }

    public Funcionario buscar(Integer id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Funcionário não encontrado!"));
    }

    @Transactional
    public Funcionario inserir(Funcionario funcionario) {
        funcionario.setId(null);
        funcionario.setDataCadastro(LocalDate.now());
        preparar(funcionario);
        return funcionarioRepository.save(funcionario);
    }

    @Transactional
    public Funcionario alterar(Integer id, Funcionario dados) {
        Funcionario atual = buscar(id);
        dados.setId(id);
        dados.setDataCadastro(atual.getDataCadastro());
        preparar(dados);
        return funcionarioRepository.save(dados);
    }

    @Transactional
    public void remover(Integer id) {
        buscar(id);
        if (pedidoRepository.existsByFuncionarioId(id)) {
            throw new RegraNegocioException("Não pode remover: o funcionário possui pedidos vinculados!");
        }
        if (usuarioRepository.existsByFuncionarioId(id)) {
            throw new RegraNegocioException("Não pode remover: o funcionário possui usuário de acesso vinculado!");
        }
        funcionarioRepository.deleteById(id);
    }

    private void preparar(Funcionario f) {
        f.setNome(f.getNome().trim());
        f.setCpf(Documentos.formatarCpf(f.getCpf()));
        // Telefone é opcional para funcionário
        if (f.getTelefone() == null || f.getTelefone().isBlank()) {
            f.setTelefone(null);
        } else {
            f.setTelefone(Documentos.formatarTelefone(f.getTelefone()));
        }

        Integer id = f.getId() == null ? 0 : f.getId();
        if (funcionarioRepository.existsByCpfAndIdNot(f.getCpf(), id)) {
            throw new RegraNegocioException("Já existe um funcionário com este CPF!");
        }
    }
}
