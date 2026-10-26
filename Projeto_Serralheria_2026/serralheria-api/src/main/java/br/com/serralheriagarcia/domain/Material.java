package br.com.serralheriagarcia.domain;

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
@Table(name = "Material")
public class Material implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    // Fornecedor (1:N) Material
    @ManyToOne(optional = false)
    @JoinColumn(name = "Id_fornecedor", nullable = false)
    private Fornecedor fornecedor;

    @Column(name = "Nome", nullable = false)
    private String nome;

    @Column(name = "Tipo", nullable = false)
    private String tipo;

    // Quantidade em estoque
    @Column(name = "Quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "Marca", nullable = false)
    private String marca;

    @Column(name = "Custo_unidade", nullable = false)
    private BigDecimal custoUnidade;

    @Column(name = "Descritivo", nullable = false)
    private String descritivo;
}
