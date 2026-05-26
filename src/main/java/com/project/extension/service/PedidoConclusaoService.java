package com.project.extension.service;

import com.project.extension.entity.Agendamento;
import com.project.extension.entity.Etapa;
import com.project.extension.entity.Pedido;
import com.project.extension.entity.Servico;
import com.project.extension.entity.Status;
import com.project.extension.entity.TipoAgendamento;
import com.project.extension.exception.RegraNegocioException;
import com.project.extension.exception.naoencontrado.EtapaNaoEncontradoException;
import com.project.extension.repository.ItemPedidoRepository;
import com.project.extension.repository.OrcamentoRepository;
import com.project.extension.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoConclusaoService {

    private final PedidoRepository pedidoRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final EtapaService etapaService;
    private final StatusService statusService;

    public void validarConclusao(Servico servico) {
        String erro = validarConclusaoSemExcecao(servico);
        if (erro != null) {
            throw new RegraNegocioException(erro);
        }
    }

    public String validarConclusaoSemExcecao(Servico servico) {
        if (servico == null || servico.getId() == null) {
            return "Serviço não encontrado para validação de conclusão.";
        }

        Pedido pedido = servico.getPedido();
        if (pedido == null || pedido.getId() == null) {
            return "O pedido não pode ser concluído pois não está vinculado corretamente ao serviço.";
        }

        Integer pedidoId = pedido.getId();

        long qtdOrcamentos = orcamentoRepository.countByPedidoIdAndAtivoTrue(pedidoId);
        if (qtdOrcamentos < 1) {
            return "O pedido não pode ser concluído pois não possui ao menos um orçamento vinculado.";
        }

        long qtdAgendamentoOrcamento = contarAgendamentosValidos(servico, TipoAgendamento.ORCAMENTO);
        if (qtdAgendamentoOrcamento < 1) {
            return "O pedido não pode ser concluído pois não possui ao menos um agendamento de orçamento.";
        }

        long qtdAgendamentoServico = contarAgendamentosValidos(servico, TipoAgendamento.SERVICO);
        if (qtdAgendamentoServico < 1) {
            return "O pedido não pode ser concluído pois não possui ao menos um agendamento de serviço.";
        }

        // item_pedido é usado para validar se o orçamento tem produtos vinculados.
        // Mesmo que os produtos do serviço sejam rastreados em agendamentoProdutos, se houver itens em item_pedido,
        // exige-se ao menos um orçamento com produtos para permitir a conclusão.
        long qtdProdutosNoPedido = itemPedidoRepository.countByPedidoId(pedidoId);
        if (qtdProdutosNoPedido > 0) {
            long qtdOrcamentosComItens = orcamentoRepository.countOrcamentosComItensByPedidoId(pedidoId);
            if (qtdOrcamentosComItens < 1) {
                return "O pedido não pode ser concluído pois não possui orçamento com produtos vinculados.";
            }
        }

        return null;
    }

    @Transactional
    public int corrigirPedidosServicoComConclusaoInvalida() {
        List<Pedido> pedidosServico = pedidoRepository.findByServicoIsNotNull();
        Etapa etapaConcluido;
        Etapa etapaAguardando;
        try {
            etapaConcluido = etapaService.buscarPorTipoAndEtapa("PEDIDO", "CONCLUÍDO");
            etapaAguardando = etapaService.buscarPorTipoAndEtapa("PEDIDO", "AGUARDANDO AGENDA DE ORÇAMENTO");
        } catch (EtapaNaoEncontradoException e) {
            log.warn("Etapas de referência ausentes — correção de pedidos ignorada: {}", e.getMessage());
            return 0;
        }
        Status statusAtivo = statusService.buscarOuCriarPorTipoENome("PEDIDO", "ATIVO");
        Status statusInativo = statusService.buscarOuCriarPorTipoENome("PEDIDO", "INATIVO");

        int corrigidos = 0;

        for (Pedido pedido : pedidosServico) {
            Servico servico = pedido.getServico();
            if (servico == null || servico.getId() == null) {
                continue;
            }

            boolean etapaConcluida = servico.getEtapa() != null
                    && "CONCLUIDO".equals(normalizar(servico.getEtapa().getNome()));
            boolean agendamentoServicoConcluido = possuiAgendamentoServicoConcluido(servico);

            if (!etapaConcluida && !agendamentoServicoConcluido) {
                continue;
            }

            String erro = validarConclusaoSemExcecao(servico);
            if (erro == null) {
                boolean mudou = false;

                if (!etapaConcluida) {
                    servico.setEtapa(etapaConcluido);
                    mudou = true;
                }
                if (!Boolean.FALSE.equals(pedido.getAtivo())) {
                    pedido.setAtivo(false);
                    mudou = true;
                }
                if (pedido.getStatus() == null || !"INATIVO".equals(normalizar(pedido.getStatus().getNome()))) {
                    pedido.setStatus(statusInativo);
                    mudou = true;
                }

                if (mudou) {
                    pedidoRepository.save(pedido);
                    corrigidos++;
                }
                continue;
            }

            boolean mudou = false;

            if (!Boolean.TRUE.equals(pedido.getAtivo())) {
                pedido.setAtivo(true);
                mudou = true;
            }
            if (pedido.getStatus() == null || isStatusConcluido(normalizar(pedido.getStatus().getNome()))) {
                pedido.setStatus(statusAtivo);
                mudou = true;
            }
            if (servico.getEtapa() == null || etapaConcluida) {
                servico.setEtapa(etapaAguardando);
                mudou = true;
            }

            if (mudou) {
                pedidoRepository.save(pedido);
                corrigidos++;
                log.warn("Pedido ID {} reaberto por conclusão inválida: {}", pedido.getId(), erro);
            }
        }

        return corrigidos;
    }

    private long contarAgendamentosValidos(Servico servico, TipoAgendamento tipo) {
        return (servico.getAgendamentos() == null ? List.<Agendamento>of() : servico.getAgendamentos()).stream()
                .filter(agendamento -> tipo.equals(agendamento.getTipoAgendamento()))
                .filter(agendamento -> !isStatusEncerrado(agendamento))
                .count();
    }

    private boolean possuiAgendamentoServicoConcluido(Servico servico) {
        return (servico.getAgendamentos() == null ? List.<Agendamento>of() : servico.getAgendamentos()).stream()
                .anyMatch(agendamento ->
                        TipoAgendamento.SERVICO.equals(agendamento.getTipoAgendamento())
                                && "CONCLUIDO".equals(normalizar(nomeStatusAgendamento(agendamento))));
    }

    private boolean isStatusEncerrado(Agendamento agendamento) {
        String status = normalizar(nomeStatusAgendamento(agendamento));
        return "CANCELADO".equals(status);
    }

    private boolean isStatusConcluido(String statusNormalizado) {
        return "CONCLUIDO".equals(statusNormalizado) || "INATIVO".equals(statusNormalizado);
    }

    private String nomeStatusAgendamento(Agendamento agendamento) {
        if (agendamento == null || agendamento.getStatusAgendamento() == null) {
            return "";
        }
        return agendamento.getStatusAgendamento().getNome();
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return "";
        }
        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('_', ' ')
                .trim()
                .toUpperCase();
    }
}
