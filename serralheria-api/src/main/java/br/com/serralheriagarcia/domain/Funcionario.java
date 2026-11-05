package br.com.serralheriagarcia.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Funcionario")
public class Funcionario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Column(name = "Nome", nullable = false)
    private String nome;

    @Column(name = "Telefone")
    private String telefone;

    @NotBlank(message = "CPF é obrigatório")
    @Column(name = "CPF", nullable = false)
    private String cpf;

    @PositiveOrZero(message = "Salário inválido")
    @Column(name = "Salario")
    private BigDecimal salario;

    @Column(name = "DataCadastro", nullable = false)
    private LocalDate dataCadastro;
}
