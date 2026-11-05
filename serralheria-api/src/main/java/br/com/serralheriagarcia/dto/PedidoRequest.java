package br.com.serralheriagarcia.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PedidoRequest(
        @NotNull(message = "Selecione o cliente!") Integer clienteId,
        @NotNull(message = "Selecione o funcionário!") Integer funcionarioId,
        @NotNull(message = "Selecione o estado!")
        @Pattern(regexp = "Pendente|Pago|Atrasado", message = "Estado inválido!") String estadoPagamento,
        @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres") String descritivo) {
}
