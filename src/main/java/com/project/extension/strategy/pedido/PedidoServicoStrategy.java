package com.project.extension.strategy.pedido;

import com.project.extension.entity.*;
import com.project.extension.exception.RegraNegocioException;
import com.project.extension.repository.OrcamentoRepository;
import com.project.extension.service.ClienteService;
import com.project.extension.service.EstoqueService;
import com.project.extension.service.EtapaService;
import com.project.extension.service.PedidoConclusaoService;
import com.project.extension.service.ServicoService;
import com.project.extension.service.StatusService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component("PEDIDO_SERVICO")
@AllArgsConstructor
@Slf4j
public class PedidoServicoStrategy implements PedidoStrategy {

    private final ServicoService servicoService;
    private final StatusService statusService;
    private final ClienteService clienteService;
    private final EtapaService etapaService;
    private final EstoqueService estoqueService;
    private final OrcamentoRepository orcamentoRepository;
    private final PedidoConclusaoService pedidoConclusaoService;

    @Override
    public Pedido criar(Pedido pedido) {
        // Validar cliente não é nulo antes de acessar getId()
        if (pedido.getCliente() == null) {
            throw new IllegalArgumentException("Pedido deve conter um cliente.");
        }

        if (pedido.getCliente().getId() == null || pedido.getCliente().getId() == 0) {
            Cliente cliente = clienteService.cadastrar(pedido.getCliente());
            cliente.setStatus("Ativo");
            pedido.setCliente(cliente);
            clienteService.atualizar(cliente, cliente.getId());
        } else {
            Cliente cliente = clienteService.buscarPorId(pedido.getCliente().getId());
            cliente.setStatus("Ativo");
            pedido.setCliente(cliente);
            clienteService.atualizar(cliente, cliente.getId());
        }

        Servico servico = pedido.getServico();
        if (servico == null) {
            throw new IllegalArgumentException("Pedido de serviço deve conter objeto Servico.");
        }

        servicoService.gerarCodigoSeNaoExistir(servico);

        servico.setPedido(pedido);
        pedido.setServico(servico);

        Status status = statusService.buscarOuCriarPorTipoENome("PEDIDO", "ATIVO");
        pedido.setStatus(status);

        Etapa etapaInicial = etapaService.buscarOuCriarPorTipoENome("PEDIDO", "AGUARDANDO AGENDA DE ORÇAMENTO");
        servico.setEtapa(etapaInicial);

        BigDecimal total = servico.getPrecoBase() != null ? servico.getPrecoBase() : BigDecimal.ZERO;
        pedido.setValorTotal(total);
        validarReservasDetalheServico(pedido.getItensPedido(), null);

        return pedido;
    }

    @Override
    public Pedido editar(Pedido origem, Pedido destino) {

        Servico antigo = origem.getServico();
        Servico novo = destino.getServico();

        if (novo == null) {
            throw new IllegalArgumentException("Pedido de serviço deve conter objeto Servico.");
        }

        if (antigo == null) {
            antigo = new Servico();
            antigo.setPedido(origem);
        }

        boolean tentandoDesativarPedido = Boolean.FALSE.equals(destino.getAtivo())
                && !Boolean.FALSE.equals(origem.getAtivo());
        boolean tentandoDesativarServico = Boolean.FALSE.equals(novo.getAtivo())
                && !Boolean.FALSE.equals(antigo.getAtivo());

        if (tentandoDesativarPedido || tentandoDesativarServico) {
            boolean possuiAgendamentoAtivo = antigo.getAgendamentos() != null && antigo.getAgendamentos().stream()
                    .filter(a -> a.getStatusAgendamento() != null && a.getStatusAgendamento().getNome() != null)
                    .anyMatch(a -> isAgendamentoBloqueante(a.getStatusAgendamento().getNome()));

            if (possuiAgendamentoAtivo) {
                throw new RegraNegocioException(
                        "Não é possível desativar o pedido/serviço enquanto existir agendamento pendente ou em andamento. Cancele o agendamento primeiro.");
            }
        }

        antigo.setNome(novo.getNome());
        antigo.setDescricao(novo.getDescricao());
        antigo.setPrecoBase(novo.getPrecoBase());
        antigo.setAtivo(novo.getAtivo());
        origem.setAtivo(destino.getAtivo());

        origem.setObservacao(destino.getObservacao());
        origem.setFormaPagamento(destino.getFormaPagamento());

        if (destino.getCliente() != null && destino.getCliente().getId() != null) {
            Cliente clienteAtual = clienteService.buscarPorId(destino.getCliente().getId());
            if (destino.getCliente().getNome() != null && !destino.getCliente().getNome().isBlank()
                    && !destino.getCliente().getNome().equals(clienteAtual.getNome())) {
                clienteAtual.setNome(destino.getCliente().getNome());
                clienteService.atualizar(clienteAtual, clienteAtual.getId());
            }
            origem.setCliente(clienteAtual);
        }

        if (novo.getEtapa() != null) {
            String nomeEtapa = novo.getEtapa().getNome();
            String nomeNorm = normalizar(nomeEtapa);

            if (nomeNorm.contains("ANALISE DO ORCAMENTO")) {
                long qtdOrcamentos = orcamentoRepository.countByPedidoIdAndAtivoTrue(origem.getId());
                if (qtdOrcamentos < 1) {
                    throw new RegraNegocioException(
                            "Para avançar para 'Análise do Orçamento', é necessário ter ao menos um orçamento cadastrado para este pedido.");
                }
            } else if (nomeNorm.contains("ORCAMENTO APROVADO")) {
                long qtdOrcamentos = orcamentoRepository.countByPedidoIdAndAtivoTrue(origem.getId());
                if (qtdOrcamentos < 1) {
                    throw new RegraNegocioException(
                            "Para avançar para 'Orçamento Aprovado', é necessário ter ao menos um orçamento cadastrado para este pedido.");
                }
            } else if (nomeNorm.contains("CONCLUIDO") || nomeNorm.contains("CONCLUÍDO")) {
                pedidoConclusaoService.validarConclusao(antigo);
            }

            Etapa etapa = etapaService.buscarPorTipoAndEtapa("PEDIDO", nomeEtapa);
            antigo.setEtapa(etapa);

            if (nomeNorm.contains("ANALISE DO ORCAMENTO")) {
                sincronizarStatusOrcamento(origem.getId(), "EM ANALISE");
            }
        }

        String nomeStatus = destino.getStatus() != null ? destino.getStatus().getNome() : "ATIVO";
        if (!Set.of("ATIVO", "INATIVO", "CANCELADO").contains(nomeStatus)) {
            nomeStatus = "ATIVO";
        }
        Status status = statusService.buscarOuCriarPorTipoENome("PEDIDO", nomeStatus);
        origem.setStatus(status);

        BigDecimal total = antigo.getPrecoBase() != null ? antigo.getPrecoBase() : BigDecimal.ZERO;
        origem.setValorTotal(total);

        antigo.setPedido(origem);
        origem.setServico(antigo);
        validarReservasDetalheServico(destino.getItensPedido(), origem.getId());

        if (destino.getItensPedido() != null) {
            origem.getItensPedido().clear();

            for (ItemPedido novoItem : destino.getItensPedido()) {
                novoItem.setPedido(origem);
                origem.getItensPedido().add(novoItem);
            }
        }

        return origem;
    }

    @Override
    public Pedido deletar(Pedido pedido) {
        return pedido;
    }

    private void validarReservasDetalheServico(List<ItemPedido> itensPedido, Integer pedidoIdIgnorado) {
        if (itensPedido == null || itensPedido.isEmpty()) {
            return;
        }

        Map<Integer, BigDecimal> quantidadePorProduto = new LinkedHashMap<>();
        Map<Integer, Produto> produtoPorId = new LinkedHashMap<>();

        for (ItemPedido item : itensPedido) {
            if (item == null || item.getEstoque() == null || item.getEstoque().getProduto() == null) {
                continue;
            }

            Produto produto = item.getEstoque().getProduto();
            Integer produtoId = produto.getId();
            BigDecimal quantidade = item.getQuantidadeSolicitada() != null
                    ? item.getQuantidadeSolicitada()
                    : BigDecimal.ZERO;

            if (produtoId == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            produtoPorId.put(produtoId, produto);
            quantidadePorProduto.merge(produtoId, quantidade, BigDecimal::add);
        }

        for (Map.Entry<Integer, BigDecimal> entry : quantidadePorProduto.entrySet()) {
            estoqueService.validarReservaDetalheServico(
                    produtoPorId.get(entry.getKey()),
                    entry.getValue(),
                    pedidoIdIgnorado
            );
        }
    }

    private String normalizar(String texto) {
        if (texto == null) return "";
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase()
                .replace('_', ' ')
                .trim();
    }

    private void sincronizarStatusOrcamento(Integer pedidoId, String novoStatusNome) {
        List<Orcamento> orcamentos = orcamentoRepository.findByPedidoIdAndAtivoTrue(pedidoId);
        if (orcamentos.isEmpty()) return;

        Status novoStatus = statusService.buscarOuCriarPorTipoENome("ORCAMENTO", novoStatusNome);
        Orcamento maisRecente = orcamentos.get(orcamentos.size() - 1);
        String statusAtual = maisRecente.getStatus() != null ? normalizar(maisRecente.getStatus().getNome()) : "";
        if (!normalizar(novoStatusNome).equals(statusAtual)) {
            maisRecente.setStatus(novoStatus);
            orcamentoRepository.save(maisRecente);
        }
    }

    private boolean isAgendamentoBloqueante(String status) {
        if (status == null)
            return false;

        String s = Normalizer.normalize(status, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase()
                .replace('_', ' ')
                .trim();

        return "PENDENTE".equals(s) || "EM ANDAMENTO".equals(s);
    }
}
