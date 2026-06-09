package com.project.extension.service;

import com.project.extension.config.RabbitMQConfig;
import com.project.extension.controller.orcamento.dto.OrcamentoItemRequestDto;
import com.project.extension.controller.orcamento.dto.OrcamentoMensagemDto;
import com.project.extension.controller.orcamento.dto.OrcamentoRequestDto;
import com.project.extension.entity.*;
import com.project.extension.exception.naoencontrado.OrcamentoNaoEncontradoException;
import com.project.extension.repository.OrcamentoRepository;
import com.project.extension.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrcamentoService {

    private final OrcamentoRepository repository;
    private final ProdutoRepository produtoRepository;
    private final PedidoService pedidoService;
    private final ClienteService clienteService;
    private final StatusService statusService;
    private final ServicoService servicoService;
    private final ServicoProdutoService servicoProdutoService;
    private final LogService logService;
    private final RabbitTemplate rabbitTemplate;
    private final OrcamentoSseService sseService;

    @Transactional(rollbackFor = Exception.class)
    public Orcamento criar(OrcamentoRequestDto request) {
        Pedido pedido = pedidoService.buscarPorId(request.pedidoId());

        Integer clienteId = request.clienteId() != null
                ? request.clienteId()
                : (pedido.getCliente() != null ? pedido.getCliente().getId() : null);

        Cliente cliente = clienteId != null
                ? clienteService.buscarPorId(clienteId)
                : pedido.getCliente();

        String statusNome = request.statusNome() != null ? request.statusNome() : "RASCUNHO";
        Status status = statusService.buscarOuCriarPorTipoENome("ORCAMENTO", statusNome);

        Orcamento orcamento = new Orcamento();
        orcamento.setPedido(pedido);
        orcamento.setCliente(cliente);
        orcamento.setStatus(status);
        orcamento.setNumeroOrcamento(request.numeroOrcamento());
        orcamento.setDataOrcamento(request.dataOrcamento());
        orcamento.setObservacoes(request.observacoes());
        orcamento.setPrazoInstalacao(request.prazoInstalacao());
        orcamento.setGarantia(request.garantia());
        orcamento.setFormaPagamento(request.formaPagamento());
        orcamento.setValorSubtotal(request.valorSubtotal() != null ? request.valorSubtotal() : BigDecimal.ZERO);
        orcamento.setValorDesconto(request.valorDesconto() != null ? request.valorDesconto() : BigDecimal.ZERO);
        orcamento.setValorTotal(request.valorTotal() != null ? request.valorTotal() : BigDecimal.ZERO);

        List<OrcamentoItem> itens = montarItensOrcamento(request.itens(), pedido);
        for (OrcamentoItem item : itens) {
            orcamento.adicionarItem(item);
        }

        Orcamento salvo = repository.save(orcamento);

        // Somente o orçamento APROVADO reflete na seção de produtos do serviço.
        // Um orçamento recém-criado normalmente é RASCUNHO/ENVIADO e NÃO sobrescreve a lista.
        if ("APROVADO".equalsIgnoreCase(statusNome)) {
            aoAprovarOrcamento(salvo);
        }

        logService.success(String.format(
                "Orçamento ID %d criado. Número: %s, Pedido: %d, Itens: %d, Total: %s.",
                salvo.getId(), salvo.getNumeroOrcamento(),
                salvo.getPedido().getId(), salvo.getItens().size(), salvo.getValorTotal()));

        return salvo;
    }

    @Transactional(rollbackFor = Exception.class)
    public Orcamento criarEGerarPdf(OrcamentoRequestDto request) {
        Orcamento salvo = criar(request);
        publicarGeracaoPdf(salvo);
        return salvo;
    }

    @Transactional(rollbackFor = Exception.class)
    public Orcamento gerarPdf(Integer id) {
        Orcamento orcamento = buscarPorId(id);
        publicarGeracaoPdf(orcamento);
        return orcamento;
    }

    private void publicarGeracaoPdf(Orcamento orcamento) {
        Integer orcamentoId = orcamento.getId();
        String numero = orcamento.getNumeroOrcamento();

        log.info("[Orçamento] Publicando geração de PDF — id={} numero='{}'", orcamentoId, numero);
        sseService.enviarEvento(orcamentoId, "GERANDO_ORCAMENTO");
        sseService.enviarEvento(orcamentoId, "GERANDO_PDF");

        OrcamentoMensagemDto mensagem = montarMensagem(orcamento);
        orcamento.setStatusFila(StatusFila.ENVIADO);
        repository.save(orcamento);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    rabbitTemplate.convertAndSend(
                            RabbitMQConfig.EXCHANGE_NAME,
                            RabbitMQConfig.ROUTING_KEY,
                            mensagem
                    );
                    log.info("[Orçamento] Mensagem publicada no RabbitMQ — id={} numero='{}'", orcamentoId, numero);
                } catch (Exception e) {
                    log.error("[Orçamento] Falha ao publicar no RabbitMQ — id={} numero='{}' motivo='{}'",
                            orcamentoId, numero, e.getMessage(), e);
                    logService.error(String.format(
                            "Falha ao publicar geração de PDF para Orçamento ID %d: %s", orcamentoId, e.getMessage()));
                    sseService.enviarEvento(orcamentoId, "ERRO");
                }
            }
        });
    }

    public Optional<Orcamento> buscarPorNumeroOrcamento(String numero) {
        return repository.findByNumeroOrcamento(numero);
    }

    public Orcamento buscarPorId(Integer id) {
        return repository.findById(id).orElseThrow(() -> {
            log.warn("[Orçamento] Não encontrado — id={}", id);
            return new OrcamentoNaoEncontradoException();
        });
    }

    public Page<Orcamento> listar(Pageable pageable) {
        return repository.findByAtivoTrueOrderByCreatedAtDesc(pageable);
    }

    public Page<Orcamento> listarPorPedido(Integer pedidoId, Pageable pageable) {
        return repository.findByPedidoIdAndAtivoTrue(pedidoId, pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    public Orcamento atualizarStatus(Integer id, String statusNome, String pdfPath) {
        statusNome = normalizarStatus(statusNome);
        Orcamento orcamento = buscarPorId(id);
        Status status = statusService.buscarOuCriarPorTipoENome("ORCAMENTO", statusNome);
        orcamento.setStatus(status);

        if (pdfPath != null && !pdfPath.isBlank()) {
            orcamento.setPdfPath(pdfPath);
            orcamento.setStatusFila(StatusFila.CONCLUIDO);
        }

        if ("ERRO".equalsIgnoreCase(statusNome)) {
            orcamento.setStatusFila(StatusFila.ERRO);
        }

        Orcamento atualizado = repository.save(orcamento);

        String eventoSse = "ERRO".equalsIgnoreCase(statusNome) ? "ERRO" : "FINALIZADO";
        sseService.enviarEvento(id, eventoSse);

        if ("ENVIADO".equalsIgnoreCase(statusNome) || "EM ANALISE".equalsIgnoreCase(statusNome)) {
            avancarEtapaSeElegivel(orcamento.getPedido(), "ANÁLISE DO ORÇAMENTO");
        } else if ("APROVADO".equalsIgnoreCase(statusNome)) {
            aoAprovarOrcamento(atualizado);
            avancarEtapaSeElegivel(orcamento.getPedido(), "AGUARDANDO AGENDA DE SERVIÇO/INSTALAÇÃO");
        }

        return atualizado;
    }

    @Transactional(rollbackFor = Exception.class)
    public Orcamento atualizar(Integer id, OrcamentoRequestDto request) {
        Orcamento orcamento = buscarPorId(id);

        String statusNomeNorm = normalizarStatus(request.statusNome());

        ifPresent(statusNomeNorm, nome -> {
            Status status = statusService.buscarOuCriarPorTipoENome("ORCAMENTO", nome);
            orcamento.setStatus(status);
        });
        ifPresent(request.numeroOrcamento(), orcamento::setNumeroOrcamento);
        ifPresent(request.dataOrcamento(),   orcamento::setDataOrcamento);
        ifPresent(request.observacoes(),     orcamento::setObservacoes);
        ifPresent(request.prazoInstalacao(), orcamento::setPrazoInstalacao);
        ifPresent(request.garantia(),        orcamento::setGarantia);
        ifPresent(request.formaPagamento(),  orcamento::setFormaPagamento);
        ifPresent(request.valorSubtotal(),   orcamento::setValorSubtotal);
        ifPresent(request.valorDesconto(),   orcamento::setValorDesconto);
        ifPresent(request.valorTotal(),      orcamento::setValorTotal);
        ifPresent(request.itens(),           itens -> substituirItens(orcamento, itens));

        Orcamento atualizado = repository.save(orcamento);

        boolean statusViraAprovado = "APROVADO".equalsIgnoreCase(statusNomeNorm);
        boolean orcamentoEstaAprovado = atualizado.getStatus() != null
                && "APROVADO".equalsIgnoreCase(atualizado.getStatus().getNome());

        // Somente o orçamento APROVADO reflete na seção de produtos do serviço.
        // Editar os itens de um orçamento já aprovado (sem mudar o status) propaga a alteração.
        // Quando o status passa a APROVADO neste mesmo request, a sincronização ocorre via aoAprovarOrcamento.
        if (request.itens() != null && orcamentoEstaAprovado && !statusViraAprovado) {
            sincronizarServicoProduto(atualizado);
        }

        logService.success(String.format(
                "Orçamento ID %d atualizado com sucesso.",
                atualizado.getId()
        ));

        if (statusNomeNorm != null) {
            if ("ENVIADO".equalsIgnoreCase(statusNomeNorm) || "EM ANALISE".equalsIgnoreCase(statusNomeNorm)) {
                avancarEtapaSeElegivel(atualizado.getPedido(), "ANÁLISE DO ORÇAMENTO");
            } else if ("APROVADO".equalsIgnoreCase(statusNomeNorm)) {
                aoAprovarOrcamento(atualizado);
                avancarEtapaSeElegivel(atualizado.getPedido(), "AGUARDANDO AGENDA DE SERVIÇO/INSTALAÇÃO");
            }
        }

        return atualizado;
    }

    private void substituirItens(Orcamento orcamento, List<OrcamentoItemRequestDto> itens) {
        orcamento.getItens().clear();
        itens.forEach(itemDto -> {
            OrcamentoItem item = new OrcamentoItem();
            item.setDescricao(itemDto.descricao());
            item.setQuantidade(itemDto.quantidade());
            item.setPrecoUnitario(itemDto.precoUnitario());
            item.setDesconto(itemDto.desconto() != null ? itemDto.desconto() : BigDecimal.ZERO);
            item.setObservacao(itemDto.observacao());
            item.setOrdem(itemDto.ordem() != null ? itemDto.ordem() : 0);
            Optional.ofNullable(itemDto.produtoId())
                    .flatMap(produtoRepository::findById)
                    .ifPresent(item::setProduto);
            orcamento.adicionarItem(item);
        });
    }

    private List<OrcamentoItem> montarItensOrcamento(List<OrcamentoItemRequestDto> itensRequest, Pedido pedido) {
        if (itensRequest != null && !itensRequest.isEmpty()) {
            return itensRequest.stream()
                    .map(this::criarItemOrcamento)
                    .toList();
        }

        // Fonte única de verdade do pedido de serviço: servico_produto.
        if (pedido.getServico() != null) {
            List<OrcamentoItem> itensServico = montarItensDeServicoProduto(pedido.getServico().getId());
            if (!itensServico.isEmpty()) {
                return itensServico;
            }
        }

        if (pedido.getItensPedido() == null || pedido.getItensPedido().isEmpty()) {
            return List.of();
        }

        List<OrcamentoItem> itens = new ArrayList<>();
        int ordem = 1;
        for (ItemPedido itemPedido : pedido.getItensPedido()) {
            OrcamentoItem item = new OrcamentoItem();
            item.setDescricao(
                    itemPedido.getEstoque() != null && itemPedido.getEstoque().getProduto() != null
                            ? itemPedido.getEstoque().getProduto().getNome()
                            : "Item do pedido"
            );
            item.setQuantidade(itemPedido.getQuantidadeSolicitada());
            item.setPrecoUnitario(itemPedido.getPrecoUnitarioNegociado());
            item.setDesconto(BigDecimal.ZERO);
            item.setObservacao(itemPedido.getObservacao());
            item.setOrdem(ordem++);

            if (itemPedido.getEstoque() != null && itemPedido.getEstoque().getProduto() != null) {
                item.setProduto(itemPedido.getEstoque().getProduto());
            }

            itens.add(item);
        }

        return itens;
    }

    private List<OrcamentoItem> montarItensDeServicoProduto(Integer servicoId) {
        List<OrcamentoItem> itens = new ArrayList<>();
        int ordem = 1;
        for (ServicoProduto sp : servicoProdutoService.listarPorServico(servicoId)) {
            OrcamentoItem item = new OrcamentoItem();
            item.setProduto(sp.getProduto());
            item.setDescricao(sp.getProduto() != null && sp.getProduto().getNome() != null
                    ? sp.getProduto().getNome()
                    : "Produto do serviço");
            item.setQuantidade(sp.getQuantidadePlanejada() != null ? sp.getQuantidadePlanejada() : BigDecimal.ZERO);
            item.setPrecoUnitario(sp.getPrecoUnitario() != null ? sp.getPrecoUnitario() : BigDecimal.ZERO);
            item.setDesconto(BigDecimal.ZERO);
            item.setObservacao(sp.getObservacao());
            item.setOrdem(sp.getOrdem() != null ? sp.getOrdem() : ordem);
            ordem++;
            itens.add(item);
        }
        return itens;
    }

    /**
     * Regra de aprovação exclusiva de orçamento.
     *
     * Um pedido pode ter vários orçamentos, mas apenas um pode ficar APROVADO. Ao aprovar um:
     * 1. Todos os demais orçamentos ativos do mesmo pedido são automaticamente marcados como RECUSADO.
     * 2. Os produtos do orçamento aprovado passam a refletir na seção de produtos do serviço
     *    ({@code servico_produto}, fonte única de verdade) — sobrescrevendo qualquer estimativa anterior.
     */
    private void aoAprovarOrcamento(Orcamento aprovado) {
        if (aprovado.getPedido() != null) {
            Status recusado = statusService.buscarOuCriarPorTipoENome("ORCAMENTO", "RECUSADO");
            List<Orcamento> irmaos = repository.findByPedidoIdAndAtivoTrue(aprovado.getPedido().getId());
            for (Orcamento outro : irmaos) {
                if (outro.getId().equals(aprovado.getId())) {
                    continue;
                }
                String statusAtual = outro.getStatus() != null ? outro.getStatus().getNome() : "";
                if ("APROVADO".equalsIgnoreCase(statusAtual) || "RECUSADO".equalsIgnoreCase(statusAtual)) {
                    continue;
                }
                outro.setStatus(recusado);
                repository.save(outro);
                logService.info(String.format(
                        "Orçamento ID %d reprovado automaticamente por aprovação do Orçamento ID %d no mesmo pedido.",
                        outro.getId(), aprovado.getId()));
            }
        }

        sincronizarServicoProduto(aprovado);
    }

    /**
     * Sincronização reversa: itens do orçamento (com produto vinculado, incluindo extras)
     * passam a ser a fonte de verdade da seção de produtos do serviço.
     */
    private void sincronizarServicoProduto(Orcamento orcamento) {
        if (orcamento.getPedido() == null || orcamento.getPedido().getServico() == null) {
            return;
        }

        Integer servicoId = orcamento.getPedido().getServico().getId();
        List<ServicoProdutoService.SyncItem> itens = orcamento.getItens().stream()
                .filter(item -> item.getProduto() != null && item.getProduto().getId() != null)
                .map(item -> new ServicoProdutoService.SyncItem(
                        item.getProduto().getId(),
                        item.getQuantidade(),
                        item.getPrecoUnitario(),
                        item.getObservacao()
                ))
                .toList();

        servicoProdutoService.sincronizarComProdutos(servicoId, itens);
    }

    private OrcamentoItem criarItemOrcamento(OrcamentoItemRequestDto itemDto) {
        OrcamentoItem item = new OrcamentoItem();
        item.setDescricao(itemDto.descricao());
        item.setQuantidade(itemDto.quantidade());
        item.setPrecoUnitario(itemDto.precoUnitario());
        item.setDesconto(itemDto.desconto() != null ? itemDto.desconto() : BigDecimal.ZERO);
        item.setObservacao(itemDto.observacao());
        item.setOrdem(itemDto.ordem() != null ? itemDto.ordem() : 0);

        if (itemDto.produtoId() != null) {
            produtoRepository.findById(itemDto.produtoId())
                    .ifPresent(item::setProduto);
        }

        return item;
    }

    private String normalizarStatus(String nome) {
        return nome != null ? nome.replace('_', ' ') : null;
    }

    private <T> void ifPresent(T value, Consumer<T> setter) {
        Optional.ofNullable(value).ifPresent(setter);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletar(Integer id) {
        Orcamento orcamento = buscarPorId(id);
        orcamento.setAtivo(false);
        repository.save(orcamento);
    }

    private void avancarEtapaSeElegivel(Pedido pedido, String nomeEtapa) {
        if (pedido.getServico() == null) return;
        long count = repository.countByPedidoIdAndAtivoTrue(pedido.getId());
        if (count >= 1) {
            servicoService.atualizarEtapaPorNome(pedido.getServico().getId(), nomeEtapa);
        }
    }

    private OrcamentoMensagemDto montarMensagem(Orcamento orcamento) {
        OrcamentoMensagemDto.ClienteMsg clienteMsg = new OrcamentoMensagemDto.ClienteMsg(
                orcamento.getCliente() != null ? orcamento.getCliente().getNome() : "N/A",
                orcamento.getCliente() != null ? orcamento.getCliente().getEmail() : "",
                orcamento.getCliente() != null ? orcamento.getCliente().getTelefone() : ""
        );

        List<OrcamentoMensagemDto.ItemMsg> itensMsg = orcamento.getItens().stream()
                .map(item -> new OrcamentoMensagemDto.ItemMsg(
                        item.getDescricao() != null ? item.getDescricao() : "",
                        item.getQuantidade() != null ? item.getQuantidade() : BigDecimal.ZERO,
                        item.getPrecoUnitario() != null ? item.getPrecoUnitario() : BigDecimal.ZERO,
                        item.getDesconto() != null ? item.getDesconto() : BigDecimal.ZERO,
                        item.getSubtotal() != null ? item.getSubtotal() : BigDecimal.ZERO,
                        item.getObservacao()
                ))
                .toList();

        List<OrcamentoMensagemDto.ProdutoInstalacaoMsg> produtosInstalacao = new ArrayList<>();
        Servico servico = orcamento.getPedido() != null ? orcamento.getPedido().getServico() : null;
        if (servico != null && servico.getAgendamentos() != null) {
            servico.getAgendamentos().stream()
                    .filter(ag -> ag.getTipoAgendamento() == TipoAgendamento.ORCAMENTO
                            && ag.getStatusAgendamento() != null
                            && !"CANCELADO".equals(ag.getStatusAgendamento().getNome()))
                    .flatMap(ag -> ag.getAgendamentoProdutos().stream())
                    .filter(ap -> ap.getProduto() != null)
                    .forEach(ap -> produtosInstalacao.add(new OrcamentoMensagemDto.ProdutoInstalacaoMsg(
                            ap.getProduto().getNome(),
                            ap.getQuantidadeReservada() != null ? ap.getQuantidadeReservada() : BigDecimal.ZERO
                    )));
        }

        return new OrcamentoMensagemDto(
                orcamento.getId().longValue(),
                orcamento.getNumeroOrcamento(),
                orcamento.getDataOrcamento() != null ? orcamento.getDataOrcamento().toString() : "",
                clienteMsg,
                itensMsg,
                orcamento.getValorSubtotal() != null ? orcamento.getValorSubtotal() : BigDecimal.ZERO,
                orcamento.getValorDesconto() != null ? orcamento.getValorDesconto() : BigDecimal.ZERO,
                orcamento.getValorTotal() != null ? orcamento.getValorTotal() : BigDecimal.ZERO,
                orcamento.getPrazoInstalacao(),
                orcamento.getGarantia(),
                orcamento.getFormaPagamento(),
                orcamento.getObservacoes(),
                produtosInstalacao
        );
    }
}
