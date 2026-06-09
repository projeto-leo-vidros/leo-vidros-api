package com.project.extension.controller.agendamento.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ConcluirAgendamentoRequestDto(
        List<ProdutoUtilizadoDto> produtos
) {
    public record ProdutoUtilizadoDto(
            @NotNull Integer produtoId,
            @NotNull BigDecimal quantidadeUtilizada
    ) {}
}
