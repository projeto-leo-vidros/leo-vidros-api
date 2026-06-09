package com.project.extension.controller.agendamento;

import com.project.extension.controller.agendamento.dto.AgendamentoMapper;
import com.project.extension.controller.agendamento.dto.AgendamentoRequestDto;
import com.project.extension.controller.agendamento.dto.AgendamentoResponseDto;
import com.project.extension.controller.pedido.servico.dto.servico.agendamento.AgendamentoServicoMapper;
import com.project.extension.controller.pedido.servico.dto.servico.agendamento.AgendamentoServicoRequestDto;
import com.project.extension.controller.pedido.servico.dto.servico.agendamento.AgendamentoServicoResponseDto;
import com.project.extension.entity.Agendamento;
import com.project.extension.service.AgendamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agendamentos")
@RequiredArgsConstructor
public class AgendamentoControllerImpl implements AgendamentoControllerDoc{

    private final AgendamentoService service;
    private final AgendamentoMapper mapper;
    private final AgendamentoServicoMapper agendamentoServicoMapper;

    @Override
    public ResponseEntity<AgendamentoResponseDto> salvar(AgendamentoRequestDto request) {
        Agendamento agendamento = mapper.toEntity(request);
        Agendamento agendamentoSalvo = service.salvar(agendamento);
        return ResponseEntity.status(201).body(mapper.toResponse(agendamentoSalvo));
    }


    @Override
    public ResponseEntity<AgendamentoResponseDto> buscarPorId(Integer id) {
        Agendamento agendamento = service.buscarPorId(id);
        return ResponseEntity.status(200).body(mapper.toResponse(agendamento));
    }

    @Override
    public ResponseEntity<Page<AgendamentoResponseDto>> buscarTodos(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.buscarTodos(pageable).map(mapper::toResponse));
    }

    @Override
    public ResponseEntity<AgendamentoResponseDto> atualizar(AgendamentoRequestDto request, Integer id) {
        Agendamento agendamentoAtualizado = mapper.toEntity(request);
        agendamentoAtualizado = service.editar(agendamentoAtualizado, id);
        return ResponseEntity.status(200).body(mapper.toResponse(agendamentoAtualizado));
    }

    @Override
    public ResponseEntity<AgendamentoServicoResponseDto> atualizarDadosBasicos(AgendamentoServicoRequestDto request, Integer id) {
        Agendamento agendamentoAtualizado = agendamentoServicoMapper.toEntity(request);
        agendamentoAtualizado = service.editarDadosBasicos(agendamentoAtualizado, id);
        return ResponseEntity.status(200).body(agendamentoServicoMapper.toResponse(agendamentoAtualizado));
    }

    @Override
    public ResponseEntity<String> deletar(Integer id) {
        service.deletar(id);
        return ResponseEntity.ok("Agendamento removido com sucesso.");
    }

    @Override
    public ResponseEntity<AgendamentoResponseDto> concluir(
            Integer id, com.project.extension.controller.agendamento.dto.ConcluirAgendamentoRequestDto request) {
        java.util.List<AgendamentoService.ProdutoUtilizado> utilizados = request.produtos() == null
                ? java.util.List.of()
                : request.produtos().stream()
                        .map(p -> new AgendamentoService.ProdutoUtilizado(p.produtoId(), p.quantidadeUtilizada()))
                        .toList();
        Agendamento concluido = service.concluirComUtilizacao(id, utilizados);
        return ResponseEntity.ok(mapper.toResponse(concluido));
    }

    @Override
    public ResponseEntity<AgendamentoResponseDto> removerFuncionario(Integer agendamentoId, Integer funcionarioId) {
        Agendamento atualizado = service.removerFuncionario(agendamentoId, funcionarioId);
        return ResponseEntity.ok(mapper.toResponse(atualizado));
    }

    @Override
    public ResponseEntity<AgendamentoResponseDto> adicionarFuncionario(Integer agendamentoId, Integer funcionarioId) {
        Agendamento atualizado = service.adicionarFuncionario(agendamentoId, funcionarioId);
        return ResponseEntity.ok(mapper.toResponse(atualizado));
    }
}
