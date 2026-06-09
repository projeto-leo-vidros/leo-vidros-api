package com.project.extension.controller.servicoproduto;

import com.project.extension.controller.servicoproduto.dto.ServicoProdutoListRequestDto;
import com.project.extension.controller.servicoproduto.dto.ServicoProdutoResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Serviço - Produtos", description = "Lista única de produtos de um pedido de serviço")
public interface ServicoProdutoControllerDoc {

    @GetMapping("/{servicoId}/produtos")
    @Operation(summary = "Listar produtos do serviço", description = """
            Lista os produtos ativos associados a um serviço.
            ---
            Fonte única de verdade da lista de produtos do pedido de serviço.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de produtos do serviço",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServicoProdutoResponseDto.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado", content = @Content())
    })
    ResponseEntity<List<ServicoProdutoResponseDto>> listar(@PathVariable Integer servicoId, Authentication authentication);

    @PutMapping("/{servicoId}/produtos")
    @Operation(summary = "Substituir produtos do serviço", description = """
            Recebe a lista completa de produtos e sincroniza (insere novos, atualiza quantidades/preços,
            desativa os removidos).
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista atualizada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ServicoProdutoResponseDto.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Corpo de requisição inválido ou regra violada", content = @Content()),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado", content = @Content())
    })
    ResponseEntity<List<ServicoProdutoResponseDto>> substituir(
            @PathVariable Integer servicoId,
            @Valid @RequestBody ServicoProdutoListRequestDto request,
            Authentication authentication);
}
