package com.project.extension.controller.solicitacao.dto;

import com.project.extension.common.validation.CPF;
import jakarta.validation.constraints.NotNull;

public record SolicitacaoRequestDto(

        @NotNull String nome,
        @NotNull String email,
        @NotNull @CPF String cpf,
        @NotNull String telefone
) {}
