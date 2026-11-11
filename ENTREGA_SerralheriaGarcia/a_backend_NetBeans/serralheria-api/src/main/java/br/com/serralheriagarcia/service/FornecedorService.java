package br.com.serralheriagarcia.service;

import br.com.serralheriagarcia.domain.Fornecedor;
import br.com.serralheriagarcia.exception.NaoEncontradoException;
import br.com.serralheriagarcia.exception.RegraNegocioException;
import br.com.serralheriagarcia.repository.FornecedorRepository;
import br.com.serralheriagarcia.repository.MaterialRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;
    private final MaterialRepository materialRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository, MaterialRepository materialRepository) {
        this.fornecedorRepository = fornecedorRepository;
        this.materialRepository = materialRepository;
    }

    public List<Fornecedor> listar() {
        return fornecedorRepository.findAllByOrderByNomeAsc();
    }

    public Fornecedor buscar(Integer id) {
        return fornecedorRepository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Fornecedor não encontrado!"));
    }

    @Transactional
    public Fornecedor inserir(Fornecedor fornecedor) {
        fornecedor.setId(null);
        fornecedor.setDataCadastro(LocalDate.now());
        preparar(fornecedor);
        return fornecedorRepository.save(fornecedor);
    }

    @Transactional
    public Fornecedor alterar(Integer id, Fornecedor dados) {
        Fornecedor atual = buscar(id);
        dados.setId(id);
        dados.setDataCadastro(atual.getDataCadastro());
        preparar(dados);
        return fornecedorRepository.save(dados);
    }

    @Transactional
    public void remover(Integer id) {
        buscar(id);
        if (materialRepository.existsByFornecedorId(id)) {
            throw new RegraNegocioException("Não pode remover: o fornecedor possui materiais vinculados!");
        }
        fornecedorRepository.deleteById(id);
    }

    private void preparar(Fornecedor f) {
        f.setNome(f.getNome().trim());
        f.setEndereco(f.getEndereco().trim());
        f.setEmail(f.getEmail().trim());
        f.setTelefone(Documentos.formatarTelefone(f.getTelefone()));
        f.setCnpj(Documentos.formatarCnpj(f.getCnpj()));

        Integer id = f.getId() == null ? 0 : f.getId();
        if (fornecedorRepository.existsByCnpjAndIdNot(f.getCnpj(), id)) {
            throw new RegraNegocioException("Já existe um fornecedor com este CNPJ!");
        }
    }
}
