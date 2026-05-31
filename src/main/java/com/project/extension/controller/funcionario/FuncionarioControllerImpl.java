package com.project.extension.controller.funcionario;

import com.project.extension.controller.funcionario.dto.AgendaFuncionarioResponseDto;
import com.project.extension.controller.funcionario.dto.FuncionarioDisponivelResponseDto;
import com.project.extension.controller.funcionario.dto.FuncionarioMapper;
import com.project.extension.controller.funcionario.dto.FuncionarioRequestDto;
import com.project.extension.controller.funcionario.dto.FuncionarioResponseDto;

import com.project.extension.entity.Funcionario;
import com.project.extension.service.FuncionarioService;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/funcionarios")
@RequiredArgsConstructor
public class FuncionarioControllerImpl implements FuncionarioControllerDoc {

    private final FuncionarioService service;
    private final FuncionarioMapper mapper;

    @Override
    @PostMapping
    public ResponseEntity<FuncionarioResponseDto> salvar(@Valid @RequestBody FuncionarioRequestDto request) {
        Funcionario funcionarioSalvar = mapper.toEntity(request);
        Funcionario funcionarioSalvo = service.cadastrar(funcionarioSalvar);
        return ResponseEntity.status(201).body(mapper.toResponse(funcionarioSalvo));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDto> buscarPorId(@PathVariable Integer id) {
        Funcionario funcionario = service.buscarPorId(id);
        return ResponseEntity.status(200).body(mapper.toResponse(funcionario));
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<FuncionarioResponseDto>> buscarTodos(
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listar(pageable).map(mapper::toResponse));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioResponseDto> atualizar(
            @Valid @RequestBody FuncionarioRequestDto request,
            @PathVariable Integer id) {

        Funcionario funcionarioAtualizar = mapper.toEntity(request);
        Funcionario funcionarioAtualizado = service.editar(funcionarioAtualizar, id);

        return ResponseEntity.status(200).body(mapper.toResponse(funcionarioAtualizado));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.ok("Funcionário deletado com sucesso.");
    }

    @Override
    @GetMapping("/{id}/agenda")
    public ResponseEntity<List<AgendaFuncionarioResponseDto>> buscarAgenda(
            @PathVariable Integer id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        List<AgendaFuncionarioResponseDto> agenda = service.buscarAgenda(id, dataInicio, dataFim);

        if (agenda.isEmpty()) {
            return ResponseEntity.status(204).build();
        }

        return ResponseEntity.ok(agenda);
    }

    @Override
    @GetMapping("/disponiveis")
    public ResponseEntity<List<FuncionarioDisponivelResponseDto>> buscarDisponiveis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime fim) {

        List<FuncionarioDisponivelResponseDto> disponiveis = service.buscarDisponiveis(data, inicio, fim);

        if (disponiveis.isEmpty()) {
            return ResponseEntity.status(204).build();
        }

        return ResponseEntity.ok(disponiveis);
    }
}
