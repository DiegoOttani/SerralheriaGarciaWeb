package br.com.serralheriagarcia.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Item")
public class Item implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    // Pedido (1:N) Item; ignorado no JSON para evitar referência circular
    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "Id_pedido", nullable = false)
    private Pedido pedido;

    // Material (1:N) Item
    @ManyToOne(optional = false)
    @JoinColumn(name = "Id_material", nullable = false)
    private Material material;

    @Column(name = "Nome", nullable = false)
    private String nome;

    @Column(name = "Quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "Valor_unidade", nullable = false)
    private BigDecimal valorUnidade;

    @Column(name = "Descritivo", nullable = false)
    private String descritivo;

    // Quantidade do material consumida (baixa no estoque)
    @Column(name = "Quantidade_material", nullable = false)
    private Integer quantidadeMaterial;

    public BigDecimal getSubtotal() {
        return valorUnidade.multiply(BigDecimal.valueOf(quantidade));
    }
}
