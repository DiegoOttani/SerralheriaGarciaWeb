package br.com.serralheriagarcia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ItemRequest(
        @NotNull(message = "Selecione o material!") Integer materialId,
        @NotBlank(message = "Nome é obrigatório") @Size(max = 100) String nome,
        @NotNull(message = "Quantidade é obrigatória") @Positive(message = "Quantidade inválida") Integer quantidade,
        @NotNull(message = "Valor é obrigatório") @PositiveOrZero(message = "Valor inválido") BigDecimal valorUnidade,
        @NotBlank(message = "Descrição é obrigatória") @Size(max = 500) String descritivo,
        @NotNull(message = "Qtd. de material é obrigatória") @PositiveOrZero(message = "Qtd. de material inválida") Integer quantidadeMaterial) {
}
