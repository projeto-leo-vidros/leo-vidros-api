package com.project.extension.controller.servicoproduto;

import com.project.extension.controller.servicoproduto.dto.ServicoProdutoListRequestDto;
import com.project.extension.controller.servicoproduto.dto.ServicoProdutoMapper;
import com.project.extension.controller.servicoproduto.dto.ServicoProdutoResponseDto;
import com.project.extension.service.ServicoProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/servicos")
@RequiredArgsConstructor
public class ServicoProdutoControllerImpl implements ServicoProdutoControllerDoc {

    private final ServicoProdutoService service;
    private final ServicoProdutoMapper mapper;

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ServicoProdutoResponseDto>> listar(Integer servicoId, Authentication authentication) {
        List<ServicoProdutoResponseDto> produtos = service.listarPorServico(servicoId).stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(produtos);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ServicoProdutoResponseDto>> substituir(
            Integer servicoId, ServicoProdutoListRequestDto request, Authentication authentication) {
        List<ServicoProdutoResponseDto> produtos = service.substituirLista(servicoId, request.produtos()).stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(produtos);
    }
}
