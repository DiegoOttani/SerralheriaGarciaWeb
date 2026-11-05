package br.com.serralheriagarcia.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Pedido")
public class Pedido implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    // Cliente (1:N) Pedido
    @ManyToOne(optional = false)
    @JoinColumn(name = "Id_Cliente", nullable = false)
    private Cliente cliente;

    // Funcionario (1:N) Pedido
    @ManyToOne(optional = false)
    @JoinColumn(name = "Id_Funcionario", nullable = false)
    private Funcionario funcionario;

    @Column(name = "DataSolicitacao", nullable = false)
    private LocalDate dataSolicitacao;

    // Calculado pela soma dos itens (ver PedidoService.recalcularTotal)
    @Column(name = "ValorTotal", nullable = false)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    // Pendente, Pago ou Atrasado
    @Column(name = "EstadoPagamento", nullable = false)
    private String estadoPagamento;

    @Column(name = "Descritivo")
    private String descritivo;

    // Pedido (1:N) Item: ao remover o pedido, seus itens também são removidos
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("id")
    private List<Item> itens = new ArrayList<>();
}
