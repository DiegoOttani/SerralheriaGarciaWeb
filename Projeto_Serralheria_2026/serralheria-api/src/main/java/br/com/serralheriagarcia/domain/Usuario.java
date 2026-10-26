package br.com.serralheriagarcia.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Usuario")
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    // Funcionário vinculado ao usuário (opcional)
    @ManyToOne
    @JoinColumn(name = "Id_funcionario")
    private Funcionario funcionario;

    @Column(name = "Nome", nullable = false)
    private String nome;

    @Column(name = "Login", nullable = false, unique = true)
    private String login;

    // Hash BCrypt; nunca é enviado nas respostas JSON
    @JsonIgnore
    @Column(name = "Senha", nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "Perfil", nullable = false)
    private Perfil perfil;
}
