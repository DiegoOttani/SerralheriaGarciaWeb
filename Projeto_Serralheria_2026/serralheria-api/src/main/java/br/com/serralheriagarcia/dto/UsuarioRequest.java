package br.com.serralheriagarcia.dto;

import br.com.serralheriagarcia.domain.Perfil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// A senha é obrigatória no cadastro; na alteração, em branco mantém a atual
public record UsuarioRequest(
        @NotBlank(message = "Nome é obrigatório") @Size(max = 100) String nome,
        @NotBlank(message = "Login é obrigatório") @Size(max = 50) String login,
        String senha,
        @NotNull(message = "Selecione o perfil!") Perfil perfil,
        Integer funcionarioId) {
}
