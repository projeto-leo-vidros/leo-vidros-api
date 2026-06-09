package com.project.extension.controller.pedido.produto.dto;

import com.project.extension.controller.valueobject.atributo.AtributoProdutoResponseDto;
import com.project.extension.controller.valueobject.metrica.MetricaEstoqueResponseDto;

import java.math.BigDecimal;
import java.util.List;

public record ProdutoResponseDto(

        Integer id,
        String nome,
        String descricao,
        String unidademedida,
                BigDecimal preco,
        Boolean ativo,
        MetricaEstoqueResponseDto metrica,
        List<AtributoProdutoResponseDto> atributos
) {
}
