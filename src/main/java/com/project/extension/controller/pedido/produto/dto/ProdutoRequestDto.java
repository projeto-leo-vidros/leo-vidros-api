package com.project.extension.controller.pedido.produto.dto;

import com.project.extension.controller.valueobject.atributo.AtributoProdutoRequestDto;
import com.project.extension.controller.valueobject.metrica.MetricaEstoqueRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record ProdutoRequestDto(
             @NotBlank String nome,
             @NotBlank String descricao,
             @NotBlank String unidademedida,
        @Positive BigDecimal preco,
             @NotNull Boolean ativo,
             @Valid @NotNull MetricaEstoqueRequestDto metrica,
             @Valid @NotNull List<AtributoProdutoRequestDto> atributos
) {
}
