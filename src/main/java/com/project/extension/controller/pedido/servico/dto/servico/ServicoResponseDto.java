package com.project.extension.controller.pedido.servico.dto.servico;

import com.project.extension.controller.valueobject.etapa.EtapaResponseDto;
import com.project.extension.controller.pedido.servico.dto.servico.agendamento.AgendamentoServicoResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ServicoResponseDto(
        Integer id,
        Integer pedidoId,
        String codigo,
        String nome,
        String descricao,
        BigDecimal precoBase,
        Boolean ativo,
        LocalDateTime createdAt,
        EtapaResponseDto etapa,
        List<AgendamentoServicoResponseDto> agendamentos
) {}

