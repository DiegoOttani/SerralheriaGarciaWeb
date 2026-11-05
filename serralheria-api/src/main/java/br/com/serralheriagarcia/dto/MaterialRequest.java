package br.com.serralheriagarcia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record MaterialRequest(
        @NotNull(message = "Selecione um fornecedor!") Integer fornecedorId,
        @NotBlank(message = "Nome é obrigatório") @Size(max = 100) String nome,
        @NotBlank(message = "Tipo é obrigatório") @Size(max = 100) String tipo,
        @NotNull(message = "Quantidade é obrigatória") @PositiveOrZero(message = "Quantidade inválida") Integer quantidade,
        @NotBlank(message = "Marca é obrigatória") @Size(max = 100) String marca,
        @NotNull(message = "Custo é obrigatório") @PositiveOrZero(message = "Custo inválido") BigDecimal custoUnidade,
        @NotBlank(message = "Descrição é obrigatória") @Size(max = 500) String descritivo) {
}
