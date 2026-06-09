package com.project.extension.controller.usuario.dto;

import com.project.extension.common.validation.CPF;
import com.project.extension.controller.valueobject.endereco.EnderecoRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record UsuarioRequestDto(

        @NotNull String nome,
        @NotNull String email,
        @NotNull @CPF String cpf,
        @NotNull String senha,
        @NotNull String telefone,
        @Valid EnderecoRequestDto endereco
        ) {}
