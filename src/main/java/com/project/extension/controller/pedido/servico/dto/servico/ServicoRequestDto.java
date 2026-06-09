package com.project.extension.controller.pedido.servico.dto.servico;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record ServicoRequestDto(
        @NotBlank String nome,
        @NotBlank String descricao,
        BigDecimal precoBase,
        Boolean ativo,
        String etapaNome
) {
}
