package br.com.serralheriagarcia.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Cliente")
public class Cliente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Column(name = "Nome", nullable = false)
    private String nome;

    @NotBlank(message = "Telefone é obrigatório")
    @Column(name = "Telefone", nullable = false)
    private String telefone;

    // CPF (pessoa física) ou CNPJ (pessoa jurídica)
    @NotBlank(message = "CPF/CNPJ é obrigatório")
    @Column(name = "CPF", nullable = false)
    private String cpf;

    @NotBlank(message = "Endereço é obrigatório")
    @Size(max = 100, message = "Endereço deve ter no máximo 100 caracteres")
    @Column(name = "Endereco", nullable = false)
    private String endereco;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Column(name = "Email", nullable = false)
    private String email;
}
