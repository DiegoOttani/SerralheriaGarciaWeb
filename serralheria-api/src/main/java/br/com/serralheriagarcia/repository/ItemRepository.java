package br.com.serralheriagarcia.repository;

import br.com.serralheriagarcia.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    boolean existsByMaterialId(Integer materialId);
}
