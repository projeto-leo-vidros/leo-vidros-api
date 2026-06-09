package com.project.extension.controller.agendamento;

import com.project.extension.controller.agendamento.dto.AgendamentoRequestDto;
import com.project.extension.controller.agendamento.dto.AgendamentoResponseDto;
import com.project.extension.controller.pedido.servico.dto.servico.agendamento.AgendamentoServicoRequestDto;
import com.project.extension.controller.pedido.servico.dto.servico.agendamento.AgendamentoServicoResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Agendamentos", description = "Operações relacionadas a agendamento de serviço e orçamento")
public interface AgendamentoControllerDoc {

    @PostMapping()
    @Operation(summary = "Salvar agendamento", description = """
            Salvar agendamento
            ---
            Salva agendamento no banco de dados
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Quando o agendamento é cadastrada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AgendamentoResponseDto.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Quando o corpo de requisição está incorreto",
                    content = @Content())
    })
    ResponseEntity<AgendamentoResponseDto> salvar(@Valid @RequestBody AgendamentoRequestDto request);

    @GetMapping("/{id}")
    @Operation(summary = "Buscar agendamento por id", description = """
           Buscar agendamento por id
            ---
           Buscar agendamento por id no banco de dados
           """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quando o agendamento é encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AgendamentoResponseDto.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Quando o agendamento não for encontrado pelo id no banco de dados",
                    content = @Content())
    })
    ResponseEntity<AgendamentoResponseDto> buscarPorId(@PathVariable Integer id);


    @GetMapping()
    @Operation(summary = "Buscar todos os agendamento", description = """
           Buscar todos os agendamento
            ---
           Buscar todos os agendamento que estão cadastrados no banco de dados
           """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista paginada de agendamentos (vazia se não houver registros)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AgendamentoResponseDto.class)
                    ))
    })
    ResponseEntity<Page<AgendamentoResponseDto>> buscarTodos(Pageable pageable);

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar agendamento", description = """
           Atualizar agendamento
            ---
           Atualizar agendamento no banco de dados
           """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quando agendamento foi atualizado com sucesso no banco de dados",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AgendamentoResponseDto.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Quando o corpo de requisição está incorreto",
                    content = @Content())
    })
    ResponseEntity<AgendamentoResponseDto> atualizar(@Valid @RequestBody AgendamentoRequestDto request, @PathVariable Integer id);

    @PutMapping("/dados-basicos/{id}")
    @Operation(summary = "Atualizar dados básicos do agendamento", description = """
           Atualizar dados básicos do agendamento
            ---
           Atualizar dados básicos do agendamento no banco de dados
           """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quando agendamento foi atualizado com sucesso no banco de dados",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AgendamentoServicoResponseDto.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Quando o corpo de requisição está incorreto",
                    content = @Content())
    })
    ResponseEntity<AgendamentoServicoResponseDto> atualizarDadosBasicos(@Valid @RequestBody AgendamentoServicoRequestDto request, @PathVariable Integer id);

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar agendamento por id", description = """
        Deleta um agendamento no banco de dados com base no id fornecido.
        """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido deletado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado com o ID fornecido",
                    content = @Content())
    })
    ResponseEntity<String> deletar(@PathVariable Integer id);

    @PutMapping("/{id}/concluir")
    @Operation(summary = "Concluir agendamento de serviço", description = """
            Conclui um agendamento de serviço informando as quantidades efetivamente utilizadas de cada produto.
            ---
            Grava a utilização na lista única do serviço, efetiva a saída de estoque do utilizado e libera o
            excedente reservado.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento concluído com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AgendamentoResponseDto.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Quando o agendamento não é de serviço ou o corpo é inválido",
                    content = @Content()),
            @ApiResponse(responseCode = "404", description = "Quando o agendamento não existe", content = @Content())
    })
    ResponseEntity<AgendamentoResponseDto> concluir(
            @PathVariable Integer id,
            @Valid @RequestBody com.project.extension.controller.agendamento.dto.ConcluirAgendamentoRequestDto request);

    @DeleteMapping("/{agendamentoId}/funcionarios/{funcionarioId}")
    @Operation(summary = "Remover funcionário de um agendamento", description = """
            Remove um funcionário de um agendamento.
            Para agendamentos do tipo SERVICO, não permite remover se for o único funcionário.
            Caso o agendamento fique sem funcionário, será cancelado automaticamente.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funcionário removido com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AgendamentoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Remoção bloqueada por regra de negócio",
                    content = @Content()),
            @ApiResponse(responseCode = "404", description = "Agendamento ou funcionário não encontrado",
                    content = @Content())
    })
    ResponseEntity<AgendamentoResponseDto> removerFuncionario(
            @PathVariable Integer agendamentoId,
            @PathVariable Integer funcionarioId
    );

    @PostMapping("/{agendamentoId}/funcionarios/{funcionarioId}")
    @Operation(summary = "Adicionar funcionário a um agendamento", description = """
            Adiciona um funcionário a um agendamento existente.
            Valida que o funcionário está ativo e não possui conflito de horário.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funcionário adicionado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AgendamentoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Conflito de horário ou funcionário inativo",
                    content = @Content()),
            @ApiResponse(responseCode = "404", description = "Agendamento ou funcionário não encontrado",
                    content = @Content())
    })
    ResponseEntity<AgendamentoResponseDto> adicionarFuncionario(
            @PathVariable Integer agendamentoId,
            @PathVariable Integer funcionarioId
    );
}