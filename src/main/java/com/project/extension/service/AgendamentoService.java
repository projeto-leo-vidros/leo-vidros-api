package com.project.extension.service;

import com.project.extension.entity.*;
import com.project.extension.exception.RegraNegocioException;
import com.project.extension.exception.naoencontrado.AgendamentoNaoEncontradoException;
import com.project.extension.exception.naoencontrado.EtapaNaoEncontradoException;
import com.project.extension.repository.AgendamentoRepository;
import com.project.extension.strategy.agendamento.AgendamentoContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository repository;
    private final EnderecoService enderecoService;
    private final FuncionarioService funcionarioService;
    private final StatusService statusService;
    private final AgendamentoContext agendamentoContext;
    private final ServicoService servicoService;
    private final EtapaService etapaService;
    private final LogService logService;
    private final EstoqueService estoqueService;
    private final com.project.extension.repository.PedidoRepository pedidoRepository;
    private final PedidoConclusaoService pedidoConclusaoService;

    @Transactional(rollbackFor = Exception.class)
    public Agendamento salvar(Agendamento agendamento) {
        if (agendamento.getFuncionarios() == null || agendamento.getFuncionarios().isEmpty()) {
            throw new RegraNegocioException("É obrigatório informar pelo menos um funcionário responsável pelo agendamento.");
        }

        if (agendamento.getServico() != null && agendamento.getServico().getId() != null) {
            Servico servico = servicoService.buscarPorId(agendamento.getServico().getId());
            agendamento.setServico(servico);
        }

        Agendamento agendamentoProcessado = agendamentoContext.processarAgendamento(agendamento);
        Agendamento agendamentoSalvo = repository.save(agendamentoProcessado);
        String mensagem = String.format("Novo Agendamento ID %d criado com sucesso. Tipo: %s, Data: %s.",
                agendamentoSalvo.getId(),
                agendamentoSalvo.getTipoAgendamento(),
                agendamentoSalvo.getDataAgendamento());
        logService.success(mensagem);
        return agendamentoSalvo;
    }

    @Transactional(rollbackFor = Exception.class)
    public Agendamento editar(Agendamento origem, Integer id) {
        Agendamento destino = buscarPorId(id);

        atualizarDadosBasicos(destino, origem);
        atualizarEndereco(destino, origem);
        atualizarHorario(destino, origem);
        atualizarProdutos(destino, origem);
        atualizarStatus(destino, origem);
        atualizarFuncionarios(destino, origem);

        return repository.save(destino);
    }

    public void deletar(Integer id) {
        Agendamento agendamento = buscarPorId(id);
        String statusAtual = agendamento.getStatusAgendamento() != null
                ? agendamento.getStatusAgendamento().getNome() : "";
        if (!statusEncerraReserva(statusAtual)) {
            liberarEstoqueAgendamento(agendamento);
        }
        Servico servico = agendamento.getServico();
        TipoAgendamento tipo = agendamento.getTipoAgendamento();
        agendamento.setServico(null);
        agendamento.getFuncionarios().clear();
        agendamento.getAgendamentoProdutos().clear();
        repository.delete(agendamento);
        repository.flush();
        if (servico != null && tipo == TipoAgendamento.ORCAMENTO) {
            reverterEtapaSeSemOrcamento(servico);
        }
        if (servico != null && tipo == TipoAgendamento.SERVICO) {
            reverterEtapaServicoSeCancelado(servico);
        }
    }

    public Agendamento buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Agendamento com ID {} não encontrado", id);
                    return new AgendamentoNaoEncontradoException();
                });
    }

    public Page<Agendamento> buscarTodos(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    public Agendamento editarDadosBasicos(Agendamento origem, Integer id) {
        Agendamento destino = buscarPorId(id);

        destino.setInicioAgendamento(origem.getInicioAgendamento());
        destino.setFimAgendamento(origem.getFimAgendamento());
        destino.setDataAgendamento(origem.getDataAgendamento());
        destino.setObservacao(origem.getObservacao());

        if (origem.getStatusAgendamento() != null) {
            Status statusAtualizado = statusService.buscarOuCriarPorTipoENome(
                    origem.getStatusAgendamento().getTipo(),
                    origem.getStatusAgendamento().getNome()
            );

            if ("EM ANDAMENTO".equals(statusAtualizado.getNome())) {
                LocalDate dataAgendamento = destino.getDataAgendamento();
                if (dataAgendamento != null && dataAgendamento.isAfter(LocalDate.now())) {
                    throw new RegraNegocioException("Não é possível iniciar um agendamento antes da data agendada.");
                }
                atualizarEtapaServico(destino.getServico(), "AGENDAMENTO EM EXECUÇÃO");
            }

            String nomeAtual = destino.getStatusAgendamento() != null ? destino.getStatusAgendamento().getNome() : "";
            if (statusEncerraReserva(statusAtualizado.getNome()) && !statusEncerraReserva(nomeAtual)) {
                encerrarReservaAgendamento(destino, statusAtualizado.getNome());
                destino.setStatusAgendamento(statusAtualizado);
                repository.save(destino);
                if (destino.getServico() != null && destino.getTipoAgendamento() == TipoAgendamento.ORCAMENTO) {
                    if ("CANCELADO".equals(statusAtualizado.getNome())) {
                        reverterEtapaSeSemOrcamento(destino.getServico());
                    }
                    // CONCLUÍDO: mantém "ORÇAMENTO AGENDADO" — OrcamentoService faz a próxima transição ao enviar o PDF
                } else if (destino.getServico() != null && destino.getTipoAgendamento() == TipoAgendamento.SERVICO) {
                    if ("CONCLUÍDO".equals(statusAtualizado.getNome()) || "CONCLUIDO".equals(statusAtualizado.getNome())) {
                        concluirEtapaServico(destino.getServico());
                    } else if ("CANCELADO".equals(statusAtualizado.getNome())) {
                        reverterEtapaServicoSeCancelado(destino.getServico());
                    }
                }
                Integer destinoId = destino.getId();
                return destinoId != null ? repository.findById(destinoId).orElse(destino) : destino;
            }

            destino.setStatusAgendamento(statusAtualizado);
        }

        return repository.save(destino);
    }

    private void atualizarDadosBasicos(Agendamento destino, Agendamento origem) {
        destino.setTipoAgendamento(origem.getTipoAgendamento());
        destino.setInicioAgendamento(origem.getInicioAgendamento());
        destino.setFimAgendamento(origem.getFimAgendamento());
        destino.setDataAgendamento(origem.getDataAgendamento());
        destino.setObservacao(origem.getObservacao());
    }

    private void atualizarEndereco(Agendamento destino, Agendamento origem) {
        if (origem.getEndereco() != null) {
            Integer enderecoId = destino.getEndereco() != null ? destino.getEndereco().getId() : null;
            if (enderecoId != null) {
                Endereco enderecoAtualizado = enderecoService.editar(origem.getEndereco(), enderecoId);
                destino.setEndereco(enderecoAtualizado);
            } else {
                Endereco novoEndereco = enderecoService.cadastrar(origem.getEndereco());
                destino.setEndereco(novoEndereco);
            }
        }
    }

    private void atualizarStatus(Agendamento destino, Agendamento origem) {
        if (origem.getStatusAgendamento() != null) {
            Status statusAtualizado = statusService.buscarOuCriarPorTipoENome(
                    origem.getStatusAgendamento().getTipo(),
                    origem.getStatusAgendamento().getNome()
            );

            if ("EM ANDAMENTO".equals(statusAtualizado.getNome())) {
                LocalDate dataAgendamento = destino.getDataAgendamento();
                if (dataAgendamento != null && dataAgendamento.isAfter(LocalDate.now())) {
                    throw new RegraNegocioException("Não é possível iniciar um agendamento antes da data agendada.");
                }
                atualizarEtapaServico(destino.getServico(), "AGENDAMENTO EM EXECUÇÃO");
            }

            String nomeAtual = destino.getStatusAgendamento() != null ? destino.getStatusAgendamento().getNome() : "";
            if (statusEncerraReserva(statusAtualizado.getNome()) && !statusEncerraReserva(nomeAtual)) {
                encerrarReservaAgendamento(destino, statusAtualizado.getNome());
                if (destino.getServico() != null && destino.getTipoAgendamento() == TipoAgendamento.ORCAMENTO) {
                    destino.setStatusAgendamento(statusAtualizado);
                    if ("CANCELADO".equals(statusAtualizado.getNome())) {
                        reverterEtapaSeSemOrcamento(destino.getServico());
                    }
                    // CONCLUÍDO: mantém "ORÇAMENTO AGENDADO" — OrcamentoService faz a próxima transição
                } else if (destino.getServico() != null && destino.getTipoAgendamento() == TipoAgendamento.SERVICO) {
                    destino.setStatusAgendamento(statusAtualizado);
                    if ("CONCLUÍDO".equals(statusAtualizado.getNome()) || "CONCLUIDO".equals(statusAtualizado.getNome())) {
                        concluirEtapaServico(destino.getServico());
                    } else if ("CANCELADO".equals(statusAtualizado.getNome())) {
                        reverterEtapaServicoSeCancelado(destino.getServico());
                    }
                }
            }

            destino.setStatusAgendamento(statusAtualizado);
        }
    }

    private void atualizarFuncionarios(Agendamento destino, Agendamento origem) {
        if (origem.getFuncionarios() != null) {
            List<Funcionario> funcionariosValidados = origem.getFuncionarios().stream()
                    .map(this::validarFuncionario)
                    .toList();

            LocalDate data = origem.getDataAgendamento() != null ? origem.getDataAgendamento() : destino.getDataAgendamento();
            LocalTime inicio = origem.getInicioAgendamento() != null ? origem.getInicioAgendamento() : destino.getInicioAgendamento();
            LocalTime fim = origem.getFimAgendamento() != null ? origem.getFimAgendamento() : destino.getFimAgendamento();

            for (Funcionario f : funcionariosValidados) {
                List<Agendamento> conflitos = repository.findConflitos(f.getId(), data, inicio, fim);
                conflitos = conflitos.stream()
                        .filter(a -> !a.getId().equals(destino.getId()))
                        .toList();

                if (!conflitos.isEmpty()) {
                    throw new RegraNegocioException(
                            String.format("Funcionário '%s' possui conflito de horário nesta data e horário.", f.getNome()));
                }
            }

            destino.getFuncionarios().clear();
            destino.getFuncionarios().addAll(funcionariosValidados);
        }
    }

    private void atualizarProdutos(Agendamento destino, Agendamento origem) {
        TipoAgendamento tipoAgendamento = origem.getTipoAgendamento() != null
                ? origem.getTipoAgendamento()
                : destino.getTipoAgendamento();

        if (tipoAgendamento == TipoAgendamento.SERVICO) {
            // Produtos de SERVICO são apenas rastreamento; não afetam estoque
            if (origem.getAgendamentoProdutos() != null) {
                destino.getAgendamentoProdutos().clear();
                for (AgendamentoProduto ap : origem.getAgendamentoProdutos()) {
                    if (ap == null || ap.getProduto() == null) continue;
                    ap.setAgendamento(destino);
                    destino.getAgendamentoProdutos().add(ap);
                }
            }
            return;
        }

        if (origem.getAgendamentoProdutos() == null) {
            return;
        }

        List<AgendamentoProduto> atualizados = new ArrayList<>();

        for (AgendamentoProduto produtoOrigem : origem.getAgendamentoProdutos()) {
            if (produtoOrigem == null || produtoOrigem.getProduto() == null || produtoOrigem.getProduto().getId() == null) {
                continue;
            }

            AgendamentoProduto produtoDestino = destino.getAgendamentoProdutos().stream()
                    .filter(item -> item.getProduto() != null
                            && item.getProduto().getId() != null
                            && item.getProduto().getId().equals(produtoOrigem.getProduto().getId()))
                    .findFirst()
                    .orElseGet(AgendamentoProduto::new);

            produtoDestino.setAgendamento(destino);
            produtoDestino.setProduto(produtoOrigem.getProduto());
            produtoDestino.setQuantidadeReservada(produtoOrigem.getQuantidadeReservada());
            produtoDestino.setQuantidadeUtilizada(produtoOrigem.getQuantidadeUtilizada());
            atualizados.add(produtoDestino);
        }

        destino.getAgendamentoProdutos().clear();
        destino.getAgendamentoProdutos().addAll(atualizados);
    }

    private Funcionario validarFuncionario(Funcionario f) {
        if (f.getId() != null) {
            return funcionarioService.buscarPorId(f.getId());
        }

        Funcionario funcionarioSalvo = funcionarioService.buscarPorTelefone(f.getTelefone());
        if (funcionarioSalvo == null) {
            funcionarioSalvo = funcionarioService.cadastrar(f);
        }
        return funcionarioSalvo;
    }

    private void atualizarHorario(Agendamento destino, Agendamento origem) {
        if(destino.getInicioAgendamento() != null && destino.getFimAgendamento() != null) {
            destino.setInicioAgendamento(origem.getInicioAgendamento());
            destino.setFimAgendamento(origem.getFimAgendamento());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Agendamento removerFuncionario(Integer agendamentoId, Integer funcionarioId) {
        Agendamento agendamento = buscarPorId(agendamentoId);

        if (agendamento.getTipoAgendamento() == TipoAgendamento.SERVICO) {
            int totalFuncionarios = repository.countFuncionariosByAgendamentoId(agendamentoId);

            if (totalFuncionarios <= 1) {
                logService.warning(String.format(
                        "Tentativa de remoção bloqueada: Funcionário ID %d é o único alocado no Agendamento ID %d (tipo SERVICO).",
                        funcionarioId, agendamentoId));
                throw new RegraNegocioException(
                        "Não é possível remover o único funcionário alocado. Isso resultaria no cancelamento do serviço.");
            }
        }

        boolean removido = agendamento.getFuncionarios()
                .removeIf(f -> f.getId().equals(funcionarioId));

        if (!removido) {
            throw new RegraNegocioException(
                    String.format("Funcionário ID %d não está alocado no Agendamento ID %d.", funcionarioId, agendamentoId));
        }

        Agendamento atualizado = repository.save(agendamento);

        if (atualizado.getFuncionarios().isEmpty()) {
            cancelarAgendamentoSemFuncionario(atualizado);
        }

        return atualizado;
    }

    @Transactional(rollbackFor = Exception.class)
    public Agendamento adicionarFuncionario(Integer agendamentoId, Integer funcionarioId) {
        Agendamento agendamento = buscarPorId(agendamentoId);
        Funcionario funcionario = funcionarioService.buscarPorId(funcionarioId);

        if (funcionario.getAtivo() == null || !funcionario.getAtivo()) {
            throw new RegraNegocioException("Funcionário inativo não pode ser alocado a um agendamento.");
        }

        boolean conflito = funcionarioService.temConflito(
                funcionarioId,
                agendamento.getDataAgendamento(),
                agendamento.getInicioAgendamento(),
                agendamento.getFimAgendamento()
        );

        if (conflito) {
            logService.warning(String.format(
                    "Conflito de agenda detectado ao tentar alocar Funcionário ID %d ao Agendamento ID %d.",
                    funcionarioId, agendamentoId));
            throw new RegraNegocioException(
                    String.format("Funcionário '%s' possui conflito de horário nesta data e horário.", funcionario.getNome()));
        }

        boolean jaAlocado = agendamento.getFuncionarios().stream()
                .anyMatch(f -> f.getId().equals(funcionarioId));

        if (jaAlocado) {
            throw new RegraNegocioException(
                    String.format("Funcionário '%s' já está alocado neste agendamento.", funcionario.getNome()));
        }

        agendamento.getFuncionarios().add(funcionario);
        Agendamento atualizado = repository.save(agendamento);

        logService.success(String.format(
                "Funcionário ID %d (%s) alocado ao Agendamento ID %d com sucesso.",
                funcionarioId, funcionario.getNome(), agendamentoId));

        return atualizado;
    }

    private void cancelarAgendamentoSemFuncionario(Agendamento agendamento) {
        Status statusCancelado = statusService.buscarOuCriarPorTipoENome("AGENDAMENTO", "CANCELADO");
        agendamento.setStatusAgendamento(statusCancelado);
        repository.save(agendamento);

        Servico servico = agendamento.getServico();
        if (servico != null) {
            TipoAgendamento tipo = agendamento.getTipoAgendamento();
            if (tipo == TipoAgendamento.ORCAMENTO) {
                reverterEtapaSeSemOrcamento(servico);
            } else if (tipo == TipoAgendamento.SERVICO) {
                reverterEtapaServicoSeCancelado(servico);
            }
        }

        logService.warning(String.format(
                "Agendamento ID %d cancelado automaticamente por ficar sem funcionário alocado.",
                agendamento.getId()));
    }

    private void concluirEtapaServico(Servico servico) {
        pedidoConclusaoService.validarConclusao(servico);

        Etapa etapaConcluido = etapaService.buscarPorTipoAndEtapa("PEDIDO", "CONCLUÍDO");
        servico.setEtapa(etapaConcluido);
        servicoService.editar(servico, servico.getId());

        Pedido pedido = servico.getPedido();
        if (pedido != null) {
            pedido.setAtivo(false);
            Status statusInativo = statusService.buscarOuCriarPorTipoENome("PEDIDO", "INATIVO");
            pedido.setStatus(statusInativo);
            pedidoRepository.save(pedido);
        }
    }

    private void atualizarEtapaServico(Servico servico, String nomeEtapa) {
        if (servico == null) return;
        try {
            Etapa etapa;
            try {
                etapa = etapaService.buscarPorTipoAndEtapa("PEDIDO", nomeEtapa);
            } catch (EtapaNaoEncontradoException e) {
                log.warn("Etapa '{}' não encontrada — realizando cadastro automático.", nomeEtapa);
                etapa = etapaService.cadastrar(new Etapa("PEDIDO", nomeEtapa));
            }
            servico.setEtapa(etapa);
            servicoService.editar(servico, servico.getId());
        } catch (Exception e) {
            log.warn("Não foi possível atualizar etapa do serviço ID {} para '{}': {}", servico.getId(), nomeEtapa, e.getMessage());
        }
    }

    private void reverterEtapaServicoSeCancelado(Servico servico) {
        List<Agendamento> servicosAtivos = repository.findAgendamentosServicoAtivosByServico(servico.getId());
        if (servicosAtivos.isEmpty()) {
            try {
                Etapa etapaAguardando = etapaService.buscarPorTipoAndEtapa("PEDIDO", "AGUARDANDO AGENDA DE SERVIÇO/INSTALAÇÃO");
                servico.setEtapa(etapaAguardando);
                servicoService.editar(servico, servico.getId());
            } catch (Exception e) {
                log.warn("Não foi possível reverter etapa do serviço ID {}: {}", servico.getId(), e.getMessage());
            }
        }
    }

    private void reverterEtapaSeSemOrcamento(Servico servico) {
        List<Agendamento> orcamentosAtivos = repository.findAtivosByServicoId(servico.getId()).stream()
                .filter(a -> TipoAgendamento.ORCAMENTO.equals(a.getTipoAgendamento()))
                .toList();
        if (orcamentosAtivos.isEmpty()) {
            try {
                Etapa etapaAguardando = etapaService.buscarPorTipoAndEtapa("PEDIDO", "AGUARDANDO AGENDA DE ORÇAMENTO");
                servico.setEtapa(etapaAguardando);
                servicoService.editar(servico, servico.getId());
            } catch (Exception e) {
                log.warn("Não foi possível reverter etapa do serviço ID {}: {}", servico.getId(), e.getMessage());
            }
        }
    }

    private void liberarEstoqueAgendamento(Agendamento agendamento) {
        if (agendamento.getAgendamentoProdutos() == null) return;
        for (AgendamentoProduto ap : agendamento.getAgendamentoProdutos()) {
            BigDecimal qtd = ap.getQuantidadeReservada();
            if (qtd != null && qtd.compareTo(BigDecimal.ZERO) > 0) {
                try {
                    estoqueService.liberarProduto(ap.getProduto(), qtd);
                } catch (Exception e) {
                    log.warn("Falha ao liberar reserva do produto ID {} no agendamento ID {}: {}",
                            ap.getProduto().getId(), agendamento.getId(), e.getMessage());
                }
            }
        }
    }

    private void encerrarReservaAgendamento(Agendamento agendamento, String nomeStatus) {
        if (agendamento.getAgendamentoProdutos() == null) return;

        boolean statusConclusao = statusConcluiReserva(nomeStatus);

        for (AgendamentoProduto ap : agendamento.getAgendamentoProdutos()) {
            BigDecimal reservada = ap.getQuantidadeReservada();
            if (reservada == null || reservada.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            try {
                if (statusConclusao) {
                    BigDecimal utilizada = ap.getQuantidadeUtilizada() != null
                            ? ap.getQuantidadeUtilizada()
                            : reservada;
                    estoqueService.finalizarReservaProduto(ap.getProduto(), reservada, utilizada);
                } else {
                    estoqueService.liberarProduto(ap.getProduto(), reservada);
                }
            } catch (Exception e) {
                log.error("Falha ao encerrar reserva do produto ID {} no agendamento ID {}: {}",
                        ap.getProduto().getId(), agendamento.getId(), e.getMessage());
                throw new RegraNegocioException(
                        String.format("Erro ao atualizar estoque do produto ID %d: %s. A alteração de status do agendamento foi revertida.",
                                ap.getProduto().getId(), e.getMessage()));
            }
        }
    }

    private boolean statusEncerraReserva(String nomeStatus) {
        return "CANCELADO".equals(nomeStatus) || "CONCLUÍDO".equals(nomeStatus) || "CONCLUIDO".equals(nomeStatus);
    }

    private boolean statusConcluiReserva(String nomeStatus) {
        return "CONCLUÍDO".equals(nomeStatus) || "CONCLUIDO".equals(nomeStatus);
    }

    public void validarConflitoAoEditar(Integer agendamentoId, LocalDate data, LocalTime inicio, LocalTime fim) {
        Agendamento agendamento = buscarPorId(agendamentoId);

        for (Funcionario f : agendamento.getFuncionarios()) {
            List<Agendamento> conflitos = repository.findConflitos(f.getId(), data, inicio, fim);
            conflitos = conflitos.stream()
                    .filter(a -> !a.getId().equals(agendamentoId))
                    .toList();

            if (!conflitos.isEmpty()) {
                logService.warning(String.format(
                        "Conflito ao editar Agendamento ID %d: Funcionário '%s' (ID %d) possui conflito no horário.",
                        agendamentoId, f.getNome(), f.getId()));
                throw new RegraNegocioException(
                        String.format("Funcionário '%s' possui conflito de horário no novo horário solicitado.", f.getNome()));
            }
        }
    }
}
