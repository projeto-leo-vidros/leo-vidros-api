package com.project.extension.controller.servicoproduto.dto;

import java.math.BigDecimal;

public record ServicoProdutoResponseDto(
        Integer id,
        Integer servicoId,
        Integer produtoId,
        String produtoNome,
        String unidadeMedida,
        BigDecimal quantidadePlanejada,
        BigDecimal quantidadeUtilizada,
        BigDecimal precoUnitario,
        String observacao,
        Integer ordem,
        Boolean ativo
) {}
