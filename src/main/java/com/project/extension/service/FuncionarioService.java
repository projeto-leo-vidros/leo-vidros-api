package com.project.extension.service;

import com.project.extension.controller.funcionario.dto.AgendaFuncionarioResponseDto;
import com.project.extension.controller.funcionario.dto.FuncionarioDisponivelResponseDto;
import com.project.extension.entity.Agendamento;
import com.project.extension.entity.Funcionario;
import com.project.extension.entity.Status;
import com.project.extension.exception.naoencontrado.FuncionarioNaoEncontradoException;
import com.project.extension.repository.AgendamentoRepository;
import com.project.extension.repository.FuncionarioRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
public class FuncionarioService {

    private final FuncionarioRepository repository;
    private final AgendamentoRepository agendamentoRepository;
    private final StatusService statusService;
    private final LogService logService;

    public Funcionario cadastrar(Funcionario funcionario) {
        return repository.save(funcionario);
    }

    public Funcionario buscarPorId(Integer id) {
        return repository.findById(id).orElseThrow(() -> {
            log.warn("Funcionário com ID {} não encontrado", id);
            return new FuncionarioNaoEncontradoException();
        });
    }

    public Funcionario buscarPorTelefone(String telefone) {
        return repository.findByTelefone(telefone);
    }

    public Page<Funcionario> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    private void atualizarCampos(Funcionario destino, Funcionario origem) {
        destino.setNome(origem.getNome());
        destino.setTelefone(origem.getTelefone());
        destino.setFuncao(origem.getFuncao());
        destino.setContrato(origem.getContrato());
        destino.setEscala(origem.getEscala());
        destino.setAtivo(origem.getAtivo());
    }

    @Transactional(rollbackFor = Exception.class)
    public Funcionario editar(Funcionario origem, Integer id) {
        Funcionario destino = this.buscarPorId(id);

        boolean eraAtivo = Boolean.TRUE.equals(destino.getAtivo());
        boolean seraInativo = !Boolean.TRUE.equals(origem.getAtivo());

        this.atualizarCampos(destino, origem);
        Funcionario funcionarioAtualizado = repository.save(destino);

        if (eraAtivo && seraInativo) {
            desvincularAgendamentosFuturos(id);
        }

        return funcionarioAtualizado;
    }

    private void desvincularAgendamentosFuturos(Integer funcionarioId) {
        List<Agendamento> agendamentos = agendamentoRepository
                .findAgendamentosFuturosAtivosByFuncionario(funcionarioId, LocalDate.now());

        if (agendamentos.isEmpty()) return;

        Status statusCancelado = statusService.buscarOuCriarPorTipoENome("AGENDAMENTO", "CANCELADO");

        for (Agendamento ag : agendamentos) {
            ag.getFuncionarios().removeIf(f -> f.getId().equals(funcionarioId));

            if (ag.getFuncionarios().isEmpty()) {
                ag.setStatusAgendamento(statusCancelado);
                logService.warning(String.format(
                        "Agendamento ID %d cancelado automaticamente: único funcionário (ID %d) foi inativado.",
                        ag.getId(), funcionarioId));
            }

            agendamentoRepository.save(ag);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletar(Integer id) {
        this.buscarPorId(id);
        agendamentoRepository.desvincularFuncionarioDeAgendamentos(id);
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<AgendaFuncionarioResponseDto> buscarAgenda(Integer funcionarioId, LocalDate dataInicio, LocalDate dataFim) {
        buscarPorId(funcionarioId);

        List<Agendamento> agendamentos = agendamentoRepository
                .findAgendamentosByFuncionarioAndPeriodo(funcionarioId, dataInicio, dataFim);

        return agendamentos.stream()
                .map(this::toAgendaResponse)
                .toList();
    }

    private AgendaFuncionarioResponseDto toAgendaResponse(Agendamento a) {
        String clienteNome = null;
        String etapaServico = null;
        String servicoNome = null;
        String servicoCodigo = null;

        if (a.getServico() != null) {
            servicoNome = a.getServico().getNome();
            servicoCodigo = a.getServico().getCodigo();

            if (a.getServico().getEtapa() != null) {
                etapaServico = a.getServico().getEtapa().getNome();
            }

            if (a.getServico().getPedido() != null && a.getServico().getPedido().getCliente() != null) {
                clienteNome = a.getServico().getPedido().getCliente().getNome();
            }
        }

        int quantidadeFuncionarios = a.getFuncionarios() != null ? a.getFuncionarios().size() : 0;

        return new AgendaFuncionarioResponseDto(
                a.getId(),
                a.getDataAgendamento(),
                a.getInicioAgendamento(),
                a.getFimAgendamento(),
                a.getTipoAgendamento() != null ? a.getTipoAgendamento().name() : null,
                a.getStatusAgendamento() != null ? a.getStatusAgendamento().getNome() : null,
                clienteNome,
                servicoNome,
                servicoCodigo,
                etapaServico,
                quantidadeFuncionarios
        );
    }

    public List<FuncionarioDisponivelResponseDto> buscarDisponiveis(LocalDate data, LocalTime inicio, LocalTime fim) {
        List<Funcionario> disponiveis = repository.findDisponiveis(data, inicio, fim);

        return disponiveis.stream()
                .map(f -> new FuncionarioDisponivelResponseDto(
                        f.getId(),
                        f.getNome(),
                        f.getTelefone(),
                        f.getFuncao(),
                        f.getEscala(),
                        f.getAtivo()
                ))
                .toList();
    }

    public boolean temConflito(Integer funcionarioId, LocalDate data, LocalTime inicio, LocalTime fim) {
        List<Agendamento> conflitos = agendamentoRepository.findConflitos(funcionarioId, data, inicio, fim);
        if (!conflitos.isEmpty()) {
            logService.warning(String.format(
                    "Conflito de agenda detectado para Funcionário ID %d em %s das %s às %s. Conflitos: %d.",
                    funcionarioId, data, inicio, fim, conflitos.size()));
        }
        return !conflitos.isEmpty();
    }
}
