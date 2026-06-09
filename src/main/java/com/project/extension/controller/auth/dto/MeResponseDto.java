package com.project.extension.controller.auth.dto;

public record MeResponseDto(
        Integer id,
        String nome,
        String email,
        Boolean firstLogin
) {}
