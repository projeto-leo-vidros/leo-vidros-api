package com.project.extension.controller.servicoproduto.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ServicoProdutoRequestDto(
        @NotNull Integer produtoId,
        @NotNull BigDecimal quantidadePlanejada,
        BigDecimal precoUnitario,
        String observacao,
        Integer ordem
) {}
