package com.project.extension.controller.servicoproduto.dto;

import jakarta.validation.Valid;

import java.util.List;

public record ServicoProdutoListRequestDto(
        @Valid List<ServicoProdutoRequestDto> produtos
) {}
