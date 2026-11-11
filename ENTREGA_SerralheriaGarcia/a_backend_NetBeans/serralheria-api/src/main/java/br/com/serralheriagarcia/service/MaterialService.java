package br.com.serralheriagarcia.service;

import br.com.serralheriagarcia.domain.Material;
import br.com.serralheriagarcia.dto.MaterialRequest;
import br.com.serralheriagarcia.exception.NaoEncontradoException;
import br.com.serralheriagarcia.exception.RegraNegocioException;
import br.com.serralheriagarcia.repository.ItemRepository;
import br.com.serralheriagarcia.repository.MaterialRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final ItemRepository itemRepository;
    private final FornecedorService fornecedorService;

    public MaterialService(MaterialRepository materialRepository, ItemRepository itemRepository,
            FornecedorService fornecedorService) {
        this.materialRepository = materialRepository;
        this.itemRepository = itemRepository;
        this.fornecedorService = fornecedorService;
    }

    public List<Material> listar() {
        return materialRepository.findAllByOrderByNomeAsc();
    }

    public Material buscar(Integer id) {
        return materialRepository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Material não encontrado!"));
    }

    @Transactional
    public Material inserir(MaterialRequest dados) {
        Material material = new Material();
        preencher(material, dados);
        return materialRepository.save(material);
    }

    @Transactional
    public Material alterar(Integer id, MaterialRequest dados) {
        Material material = buscar(id);
        preencher(material, dados);
        return materialRepository.save(material);
    }

    @Transactional
    public void remover(Integer id) {
        buscar(id);
        if (itemRepository.existsByMaterialId(id)) {
            throw new RegraNegocioException("Não pode remover: o material está sendo usado em itens de pedidos!");
        }
        materialRepository.deleteById(id);
    }

    private void preencher(Material m, MaterialRequest dados) {
        m.setFornecedor(fornecedorService.buscar(dados.fornecedorId()));
        m.setNome(dados.nome().trim());
        m.setTipo(dados.tipo().trim());
        m.setQuantidade(dados.quantidade());
        m.setMarca(dados.marca().trim());
        m.setCustoUnidade(dados.custoUnidade());
        m.setDescritivo(dados.descritivo().trim());
    }
}
