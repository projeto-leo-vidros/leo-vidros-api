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
    private final LogService logService;
    private final RabbitTemplate rabbitTemplate;
    private final OrcamentoSseService sseService;

    @Transactional
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

        logService.success(String.format(
                "Orçamento ID %d criado com sucesso. Número: %s, Pedido: %d, Total: %s.",
                salvo.getId(),
                salvo.getNumeroOrcamento(),
                salvo.getPedido().getId(),
                salvo.getValorTotal()
        ));

        return salvo;
    }

    @Transactional
    public Orcamento criarEGerarPdf(OrcamentoRequestDto request) {
        Orcamento salvo = criar(request);
        publicarGeracaoPdf(salvo);
        return salvo;
    }

    @Transactional
    public Orcamento gerarPdf(Integer id) {
        Orcamento orcamento = buscarPorId(id);
        publicarGeracaoPdf(orcamento);
        return orcamento;
    }

    private void publicarGeracaoPdf(Orcamento orcamento) {
        sseService.enviarEvento(orcamento.getId(), "GERANDO_ORCAMENTO");
        sseService.enviarEvento(orcamento.getId(), "GERANDO_PDF");

        OrcamentoMensagemDto mensagem = montarMensagem(orcamento);
        orcamento.setStatusFila(StatusFila.ENVIADO);
        repository.save(orcamento);

        Integer orcamentoId = orcamento.getId();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    rabbitTemplate.convertAndSend(
                            RabbitMQConfig.EXCHANGE_NAME,
                            RabbitMQConfig.ROUTING_KEY,
                            mensagem
                    );
                    logService.info(String.format(
                            "Mensagem de geração de PDF publicada na fila para o Orçamento ID %d.", orcamentoId
                    ));
                } catch (Exception e) {
                    logService.error(String.format(
                            "Falha ao publicar mensagem no RabbitMQ para Orçamento ID %d: %s",
                            orcamentoId, e.getMessage()
                    ));
                    sseService.enviarEvento(orcamentoId, "ERRO");
                }
            }
        });
    }

    public Orcamento buscarPorId(Integer id) {
        return repository.findById(id).orElseThrow(() -> {
            String msg = String.format("Orçamento ID %d não encontrado.", id);
            logService.error(msg);
            return new OrcamentoNaoEncontradoException();
        });
    }

    public Page<Orcamento> listar(Pageable pageable) {
        Page<Orcamento> orcamentos = repository.findByAtivoTrueOrderByCreatedAtDesc(pageable);
        logService.info(String.format("Listagem de orçamentos: %d registros.", orcamentos.getTotalElements()));
        return orcamentos;
    }

    public Page<Orcamento> listarPorPedido(Integer pedidoId, Pageable pageable) {
        return repository.findByPedidoIdAndAtivoTrue(pedidoId, pageable);
    }

    @Transactional
    public Orcamento atualizarStatus(Integer id, String statusNome, String pdfPath) {
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

        logService.info(String.format(
                "Status do Orçamento ID %d atualizado para '%s'.",
                id, statusNome
        ));

        String eventoSse = "ERRO".equalsIgnoreCase(statusNome) ? "ERRO" : "FINALIZADO";
        sseService.enviarEvento(id, eventoSse);

        if ("ENVIADO".equalsIgnoreCase(statusNome) || "EM ANALISE".equalsIgnoreCase(statusNome)) {
            avancarEtapaSeElegivel(orcamento.getPedido(), "ANÁLISE DO ORÇAMENTO");
        } else if ("APROVADO".equalsIgnoreCase(statusNome)) {
            avancarEtapaSeElegivel(orcamento.getPedido(), "ORÇAMENTO APROVADO");
        }

        return atualizado;
    }

    @Transactional
    public Orcamento atualizar(Integer id, OrcamentoRequestDto request) {
        Orcamento orcamento = buscarPorId(id);

        ifPresent(request.statusNome(), nome -> {
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

        logService.success(String.format(
                "Orçamento ID %d atualizado com sucesso.",
                atualizado.getId()
        ));

        if (request.statusNome() != null) {
            if ("ENVIADO".equalsIgnoreCase(request.statusNome()) || "EM ANALISE".equalsIgnoreCase(request.statusNome())) {
                avancarEtapaSeElegivel(atualizado.getPedido(), "ANÁLISE DO ORÇAMENTO");
            } else if ("APROVADO".equalsIgnoreCase(request.statusNome())) {
                avancarEtapaSeElegivel(atualizado.getPedido(), "ORÇAMENTO APROVADO");
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

    private <T> void ifPresent(T value, Consumer<T> setter) {
        Optional.ofNullable(value).ifPresent(setter);
    }

    @Transactional
    public void deletar(Integer id) {
        Orcamento orcamento = buscarPorId(id);
        orcamento.setAtivo(false);
        repository.save(orcamento);
        logService.info(String.format("Orçamento ID %d desativado.", id));
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
                        item.getDescricao(),
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
