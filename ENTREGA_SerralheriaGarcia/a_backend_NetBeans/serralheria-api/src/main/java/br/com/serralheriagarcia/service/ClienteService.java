package br.com.serralheriagarcia.service;

import br.com.serralheriagarcia.domain.Cliente;
import br.com.serralheriagarcia.exception.NaoEncontradoException;
import br.com.serralheriagarcia.exception.RegraNegocioException;
import br.com.serralheriagarcia.repository.ClienteRepository;
import br.com.serralheriagarcia.repository.PedidoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;

    public ClienteService(ClienteRepository clienteRepository, PedidoRepository pedidoRepository) {
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public List<Cliente> listar() {
        return clienteRepository.findAllByOrderByNomeAsc();
    }

    public Cliente buscar(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Cliente não encontrado!"));
    }

    @Transactional
    public Cliente inserir(Cliente cliente) {
        cliente.setId(null);
        preparar(cliente);
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente alterar(Integer id, Cliente dados) {
        buscar(id);
        dados.setId(id);
        preparar(dados);
        return clienteRepository.save(dados);
    }

    @Transactional
    public void remover(Integer id) {
        buscar(id);
        if (pedidoRepository.existsByClienteId(id)) {
            throw new RegraNegocioException("Não pode remover: o cliente possui pedidos vinculados!");
        }
        clienteRepository.deleteById(id);
    }

    // Valida e formata os campos antes de gravar
    private void preparar(Cliente c) {
        c.setNome(c.getNome().trim());
        c.setEndereco(c.getEndereco().trim());
        c.setEmail(c.getEmail().trim());
        c.setTelefone(Documentos.formatarTelefone(c.getTelefone()));
        c.setCpf(Documentos.formatarCpfCnpj(c.getCpf()));

        Integer id = c.getId() == null ? 0 : c.getId();
        if (clienteRepository.existsByCpfAndIdNot(c.getCpf(), id)) {
            throw new RegraNegocioException("Já existe um cliente com este CPF/CNPJ!");
        }
    }
}
