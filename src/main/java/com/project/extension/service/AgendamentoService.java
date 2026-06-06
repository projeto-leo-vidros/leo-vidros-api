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
    private final ServicoProdutoService servicoProdutoService;
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
                aplicarEtapaEmExecucao(destino);
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
                aplicarEtapaEmExecucao(destino);
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
            // A lista de produtos do SERVIÇO vive em servico_produto (fonte única de verdade).
            // Não persistimos mais agendamento_produto para SERVIÇO.
            destino.getAgendamentoProdutos().clear();
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

    /**
     * Ao colocar um agendamento "EM ANDAMENTO", a etapa do serviço depende do TIPO do agendamento:
     * - ORÇAMENTO (vistoria): permanece no fluxo de orçamento ("ORÇAMENTO AGENDADO"), pois ainda não
     *   há serviço/instalação em curso.
     * - SERVIÇO/INSTALAÇÃO: avança para "AGENDAMENTO EM EXECUÇÃO".
     * O status persistido é único ("EM ANDAMENTO"); só a etapa é diferenciada pelo tipo.
     */
    private void aplicarEtapaEmExecucao(Agendamento agendamento) {
        if (agendamento == null || agendamento.getServico() == null) {
            return;
        }
        String nomeEtapa = agendamento.getTipoAgendamento() == TipoAgendamento.ORCAMENTO
                ? "ORÇAMENTO AGENDADO"
                : "AGENDAMENTO EM EXECUÇÃO";
        atualizarEtapaServico(agendamento.getServico(), nomeEtapa);
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

    /**
     * Conciliação de registros legados: corrige serviços cuja etapa ficou gravada como
     * "AGENDAMENTO EM EXECUÇÃO" (região do fluxo de serviço) quando, na verdade, o que está em
     * execução é um agendamento de ORÇAMENTO (vistoria). Esses casos foram gerados antes da
     * diferenciação de etapa por tipo de agendamento.
     *
     * Critério conservador: só corrige quando NÃO há agendamento de SERVIÇO em andamento e EXISTE
     * um agendamento de ORÇAMENTO ativo (pendente/em andamento). A etapa volta para "ORÇAMENTO AGENDADO".
     */
    @Transactional
    public int corrigirEtapaOrcamentoEmExecucao() {
        int corrigidos = 0;
        for (Servico servico : servicoService.listarPorEtapa("AGENDAMENTO EM EXECUÇÃO")) {
            boolean servicoEmAndamento = repository.findAgendamentosServicoAtivosByServico(servico.getId()).stream()
                    .anyMatch(a -> a.getStatusAgendamento() != null
                            && "EM ANDAMENTO".equals(a.getStatusAgendamento().getNome()));
            if (servicoEmAndamento) {
                continue;
            }

            boolean orcamentoEmAberto = repository.findAtivosByServicoId(servico.getId()).stream()
                    .anyMatch(a -> a.getTipoAgendamento() == TipoAgendamento.ORCAMENTO
                            && a.getStatusAgendamento() != null
                            && !"CONCLUÍDO".equals(a.getStatusAgendamento().getNome())
                            && !"CONCLUIDO".equals(a.getStatusAgendamento().getNome()));
            if (orcamentoEmAberto) {
                atualizarEtapaServico(servico, "ORÇAMENTO AGENDADO");
                corrigidos++;
            }
        }
        return corrigidos;
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
        // Apenas agendamentos de SERVIÇO reservam estoque (via servico_produto).
        if (agendamento.getTipoAgendamento() != TipoAgendamento.SERVICO || agendamento.getServico() == null) {
            return;
        }

        for (ServicoProduto sp : servicoProdutoService.listarPorServico(agendamento.getServico().getId())) {
            BigDecimal planejada = sp.getQuantidadePlanejada();
            if (sp.getProduto() == null || planejada == null || planejada.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            try {
                estoqueService.liberarProduto(sp.getProduto(), planejada);
            } catch (Exception e) {
                log.warn("Falha ao liberar reserva do produto ID {} no agendamento ID {}: {}",
                        sp.getProduto().getId(), agendamento.getId(), e.getMessage());
            }
        }
    }

    private void encerrarReservaAgendamento(Agendamento agendamento, String nomeStatus) {
        // Apenas agendamentos de SERVIÇO movimentam estoque na conclusão/cancelamento.
        if (agendamento.getTipoAgendamento() != TipoAgendamento.SERVICO || agendamento.getServico() == null) {
            return;
        }

        boolean statusConclusao = statusConcluiReserva(nomeStatus);
        Integer servicoId = agendamento.getServico().getId();

        for (ServicoProduto sp : servicoProdutoService.listarPorServico(servicoId)) {
            BigDecimal planejada = sp.getQuantidadePlanejada();
            if (sp.getProduto() == null || planejada == null || planejada.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            try {
                if (statusConclusao) {
                    BigDecimal utilizada = sp.getQuantidadeUtilizada() != null
                            ? sp.getQuantidadeUtilizada()
                            : planejada;
                    estoqueService.finalizarReservaProduto(sp.getProduto(), planejada, utilizada);
                    servicoProdutoService.marcarUtilizacao(servicoId, sp.getProduto().getId(), utilizada);
                } else {
                    estoqueService.liberarProduto(sp.getProduto(), planejada);
                }
            } catch (Exception e) {
                log.error("Falha ao encerrar reserva do produto ID {} no agendamento ID {}: {}",
                        sp.getProduto().getId(), agendamento.getId(), e.getMessage());
                throw new RegraNegocioException(
                        String.format("Erro ao atualizar estoque do produto ID %d: %s. A alteração de status do agendamento foi revertida.",
                                sp.getProduto().getId(), e.getMessage()));
            }
        }
    }

    /**
     * Conclui um agendamento de SERVIÇO informando as quantidades efetivamente utilizadas.
     * Grava a utilização na lista única (servico_produto), efetiva a saída de estoque do
     * utilizado e libera o excedente reservado.
     */
    @Transactional
    public Agendamento concluirComUtilizacao(Integer agendamentoId, List<ProdutoUtilizado> utilizados) {
        Agendamento agendamento = buscarPorId(agendamentoId);

        if (agendamento.getTipoAgendamento() != TipoAgendamento.SERVICO) {
            throw new RegraNegocioException("Apenas agendamentos de serviço podem ser concluídos com informe de utilização.");
        }
        if (agendamento.getServico() == null) {
            throw new RegraNegocioException("Agendamento sem serviço vinculado não pode ser concluído.");
        }

        Integer servicoId = agendamento.getServico().getId();

        if (utilizados != null) {
            for (ProdutoUtilizado pu : utilizados) {
                if (pu == null || pu.produtoId() == null) continue;
                servicoProdutoService.marcarUtilizacao(servicoId, pu.produtoId(), pu.quantidadeUtilizada());
            }
        }

        Status statusConcluido = statusService.buscarOuCriarPorTipoENome("AGENDAMENTO", "CONCLUÍDO");
        String nomeAtual = agendamento.getStatusAgendamento() != null ? agendamento.getStatusAgendamento().getNome() : "";

        if (!statusEncerraReserva(nomeAtual)) {
            encerrarReservaAgendamento(agendamento, statusConcluido.getNome());
        }

        agendamento.setStatusAgendamento(statusConcluido);
        Agendamento salvo = repository.save(agendamento);

        concluirEtapaServico(agendamento.getServico());

        logService.success(String.format("Agendamento de serviço ID %d concluído com informe de utilização.", agendamentoId));
        return salvo;
    }

    public record ProdutoUtilizado(Integer produtoId, BigDecimal quantidadeUtilizada) {}

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
