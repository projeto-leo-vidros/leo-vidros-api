package com.project.extension.controller.servicoproduto.dto;

import com.project.extension.entity.Produto;
import com.project.extension.entity.ServicoProduto;
import org.springframework.stereotype.Component;

@Component
public class ServicoProdutoMapper {

    public ServicoProdutoResponseDto toResponse(ServicoProduto sp) {
        if (sp == null) return null;

        Produto produto = sp.getProduto();

        return new ServicoProdutoResponseDto(
                sp.getId(),
                sp.getServico() != null ? sp.getServico().getId() : null,
                produto != null ? produto.getId() : null,
                produto != null ? produto.getNome() : null,
                produto != null ? produto.getUnidademedida() : null,
                sp.getQuantidadePlanejada(),
                sp.getQuantidadeUtilizada(),
                sp.getPrecoUnitario(),
                sp.getObservacao(),
                sp.getOrdem(),
                sp.getAtivo()
        );
    }
}
