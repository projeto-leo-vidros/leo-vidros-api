package com.project.extension.service;

import com.project.extension.entity.*;
import com.project.extension.exception.naoencontrado.EstoqueNaoEncontradoException;
import com.project.extension.exception.naoencontrado.ProdutoNaoEncontradoException;
import com.project.extension.exception.naopodesernegativo.EstoqueNaoPodeSerNegativoException;
import com.project.extension.repository.AgendamentoProdutoRepository;
import com.project.extension.repository.EstoqueRepository;
import com.project.extension.repository.ItemPedidoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@AllArgsConstructor
public class EstoqueService {

    private final EstoqueRepository repository;
    private final AgendamentoProdutoRepository agendamentoProdutoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final ProdutoService produtoService;
    private final HistoricoEstoqueService historicoService;
    private final UsuarioService usuarioService;

    public Estoque entrada(Estoque request) {
        return movimentarEstoque(request, TipoMovimentacao.ENTRADA, null, null, null, null);
    }

    public Estoque entrada(Estoque request, String observacao) {
        return movimentarEstoque(request, TipoMovimentacao.ENTRADA, null, null, null, observacao);
    }

    public Estoque saida(Estoque request) {
        return movimentarEstoque(request, TipoMovimentacao.SAIDA, null, OrigemMovimentacao.MANUAL, null, null);
    }

    public Estoque saida(Estoque request, String observacao) {
        return movimentarEstoque(request, TipoMovimentacao.SAIDA, null, OrigemMovimentacao.MANUAL, null, observacao);
    }

    public void saida(Estoque request, Pedido pedido) {
        movimentarEstoque(request, TipoMovimentacao.SAIDA, pedido, OrigemMovimentacao.PEDIDO, null, null);
    }

    public Estoque saida(Estoque request, OrigemMovimentacao origemMovimentacao, MotivoPerda motivoPerda) {
        return movimentarEstoque(request, TipoMovimentacao.SAIDA, null, origemMovimentacao, motivoPerda, null);
    }

    @Transactional(rollbackFor = Exception.class)
    private Estoque movimentarEstoque(
            Estoque request,
            TipoMovimentacao tipo,
            Pedido pedido,
            OrigemMovimentacao origem,
            MotivoPerda motivoPerda,
            String observacao
    ) {
        validarRequest(request);

        BigDecimal quantidade = validarQuantidade(request.getQuantidadeTotal());

        Produto produto = produtoService.buscarPorId(request.getProduto().getId());
        Estoque estoque = buscarOuCriarEstoque(produto, request.getLocalizacao());
        sincronizarReservaComAgendamentosAtivos(estoque);

        BigDecimal totalAtual = estoque.getQuantidadeTotal();
        BigDecimal reservado = estoque.getReservado() != null ? estoque.getReservado() : BigDecimal.ZERO;
        BigDecimal novoTotal = calcularNovoTotal(
                tipo,
                totalAtual,
                estoque.getQuantidadeDisponivel(),
                quantidade,
                produto,
                reservado,
                origem != null ? origem : OrigemMovimentacao.MANUAL
        );

        aplicarNovoTotal(estoque, novoTotal);
        atualizarStatusProduto(produto, estoque.getQuantidadeDisponivel());

        Estoque salvo = repository.save(estoque);

        registrarHistorico(salvo, tipo, pedido, origem, motivoPerda, quantidade, produto, observacao);

        return salvo;
    }

    private void validarRequest(Estoque request) {
        if (request.getProduto() == null) {
            log.error("Para movimentar estoque, deve ter um produto!");
            throw new IllegalArgumentException("Produto é obrigatório.");
        }
    }

    private Estoque buscarOuCriarEstoque(Produto produto, String localizacao) {
        return repository.findByProdutoAndLocalizacaoForUpdate(produto, localizacao)
                .orElseGet(() -> {
                    Estoque novo = new Estoque();
                    novo.setProduto(produto);
                    novo.setLocalizacao(localizacao);
                    novo.setQuantidadeTotal(BigDecimal.ZERO);
                    novo.setQuantidadeDisponivel(BigDecimal.ZERO);
                    novo.setReservado(BigDecimal.ZERO);
                    return novo;
                });
    }

    private BigDecimal validarQuantidade(BigDecimal quantidade) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("A quantidade movimentada deve ser maior que zero.");
        }
        return quantidade;
    }

    private BigDecimal calcularNovoTotal(
            TipoMovimentacao tipo,
            BigDecimal totalAtual,
            BigDecimal disponivelAtual,
            BigDecimal quantidade,
            Produto produto,
            BigDecimal reservado,
            OrigemMovimentacao origem
    ) {
        if (tipo == TipoMovimentacao.SAIDA) {
            if (quantidade.compareTo(disponivelAtual) > 0) {
                throw new EstoqueNaoPodeSerNegativoException(String.format(
                        "Estoque insuficiente para '%s'. Disponível: %s, solicitado: %s.",
                        produto.getNome(),
                        disponivelAtual.stripTrailingZeros().toPlainString(),
                        quantidade.stripTrailingZeros().toPlainString()
                ));
            }

            return totalAtual.subtract(quantidade);
        }

        return totalAtual.add(quantidade);
    }

    private void aplicarNovoTotal(Estoque estoque, BigDecimal novoTotal) {
        estoque.setQuantidadeTotal(novoTotal);
        estoque.setQuantidadeDisponivel(novoTotal.subtract(estoque.getReservado()));
    }

    private void atualizarStatusProduto(Produto produto, BigDecimal quantidadeDisponivel) {
        if (produto == null || quantidadeDisponivel == null) return;

        boolean estavaAtivo = Boolean.TRUE.equals(produto.getAtivo());
        boolean deveFicarAtivo = quantidadeDisponivel.compareTo(BigDecimal.ZERO) > 0;

        if (deveFicarAtivo != estavaAtivo) {
            produto.setAtivo(deveFicarAtivo);
            produtoService.editar(produto, produto.getId());
        }
    }

    private void registrarHistorico(
            Estoque estoque,
            TipoMovimentacao tipo,
            Pedido pedido,
            OrigemMovimentacao origem,
            MotivoPerda motivoPerda,
            BigDecimal quantidade,
            Produto produto,
            String observacaoCustom
    ) {
        Usuario usuario = getUsuarioLogado();

        HistoricoEstoque historico = new HistoricoEstoque();
        historico.setEstoque(estoque);
        historico.setUsuario(usuario);
        historico.setTipoMovimentacao(tipo);
        historico.setQuantidade(quantidade);
        historico.setQuantidadeAtual(estoque.getQuantidadeDisponivel());

        if (tipo == TipoMovimentacao.SAIDA) {
            if (pedido != null) {
                historico.setPedido(pedido);
                historico.setOrigem(OrigemMovimentacao.PEDIDO);
            } else if (origem == OrigemMovimentacao.PERDA) {
                historico.setOrigem(OrigemMovimentacao.PERDA);
                historico.setMotivoPerda(motivoPerda);
            } else {
                historico.setOrigem(origem != null ? origem : OrigemMovimentacao.MANUAL);
            }
        } else {
            historico.setOrigem(origem != null ? origem : OrigemMovimentacao.MANUAL);
        }

        String obsGerada = tipo == TipoMovimentacao.ENTRADA
                ? String.format("Entrada de %f unidades de '%s' em '%s'", quantidade, produto.getNome(), estoque.getLocalizacao())
                : String.format("Saída de %f unidades de '%s' em '%s'", quantidade, produto.getNome(), estoque.getLocalizacao());
        historico.setObservacao(observacaoCustom != null && !observacaoCustom.isBlank()
                ? observacaoCustom + " - " + obsGerada
                : obsGerada);

        historicoService.cadastrar(historico);
    }

    public Page<Estoque> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Estoque buscarPorId(Integer id) {
        Estoque estoque = repository.findById(id)
                .orElseThrow(EstoqueNaoEncontradoException::new);
        sincronizarReservaComAgendamentosAtivos(estoque);
        return estoque;
    }

    @Transactional(rollbackFor = Exception.class)
    public void reservarProduto(Produto produto, BigDecimal quantidade) {
        Estoque estoque = repository.findByProdutoIdForUpdate(produto.getId())
                .orElseThrow(EstoqueNaoEncontradoException::new);
        sincronizarReservaComAgendamentosAtivos(estoque);

        BigDecimal reservadoAtual = estoque.getReservado() != null ? estoque.getReservado() : BigDecimal.ZERO;
        BigDecimal disponivel = estoque.getQuantidadeDisponivel() != null ? estoque.getQuantidadeDisponivel() : BigDecimal.ZERO;

        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("A quantidade reservada deve ser maior que zero.");
        }

        if (quantidade.compareTo(disponivel) > 0) {
            throw new EstoqueNaoPodeSerNegativoException(String.format(
                    "Estoque insuficiente para reservar '%s'. Disponivel: %s, solicitado: %s.",
                    produto.getNome(),
                    disponivel.stripTrailingZeros().toPlainString(),
                    quantidade.stripTrailingZeros().toPlainString()
            ));
        }

        BigDecimal novoReservado = reservadoAtual.add(quantidade);
        estoque.setReservado(novoReservado);
        estoque.setQuantidadeDisponivel(estoque.getQuantidadeTotal().subtract(novoReservado));

        repository.save(estoque);
        atualizarStatusProduto(produto, estoque.getQuantidadeDisponivel());
    }

    private Usuario getUsuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return usuarioService.buscarPorEmail(email);
    }

    @Transactional(rollbackFor = Exception.class)
    public void liberarProduto(Produto produto, BigDecimal quantidade) {
        Estoque estoque = repository.findByProdutoIdForUpdate(produto.getId())
                .orElseThrow(EstoqueNaoEncontradoException::new);
        sincronizarReservaComAgendamentosAtivos(estoque);

        BigDecimal reservadoAtual = estoque.getReservado() != null ? estoque.getReservado() : BigDecimal.ZERO;

        BigDecimal novaReserva = reservadoAtual.subtract(quantidade);
        if (novaReserva.compareTo(BigDecimal.ZERO) < 0) novaReserva = BigDecimal.ZERO;

        estoque.setReservado(novaReserva);
        estoque.setQuantidadeDisponivel(estoque.getQuantidadeTotal().subtract(novaReserva));

        repository.save(estoque);
        atualizarStatusProduto(estoque.getProduto(), estoque.getQuantidadeDisponivel());
    }

    @Transactional(rollbackFor = Exception.class)
    public void finalizarReservaProduto(Produto produto, BigDecimal quantidadeReservada, BigDecimal quantidadeUtilizada) {
        Estoque estoque = repository.findByProdutoIdForUpdate(produto.getId())
                .orElseThrow(EstoqueNaoEncontradoException::new);

        sincronizarReservaComAgendamentosAtivos(estoque);

        BigDecimal reservadoAtual = estoque.getReservado() != null ? estoque.getReservado() : BigDecimal.ZERO;
        BigDecimal totalAtual = estoque.getQuantidadeTotal() != null ? estoque.getQuantidadeTotal() : BigDecimal.ZERO;
        BigDecimal reservada = quantidadeReservada != null ? quantidadeReservada : BigDecimal.ZERO;
        BigDecimal utilizada = quantidadeUtilizada != null ? quantidadeUtilizada : BigDecimal.ZERO;

        if (reservada.compareTo(BigDecimal.ZERO) < 0 || utilizada.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("As quantidades de reserva e uso nao podem ser negativas.");
        }

        if (utilizada.compareTo(reservada) > 0) {
            utilizada = reservada;
        }

        BigDecimal novoReservado = reservadoAtual.subtract(reservada);
        if (novoReservado.compareTo(BigDecimal.ZERO) < 0) {
            novoReservado = BigDecimal.ZERO;
        }

        BigDecimal novoTotal = totalAtual.subtract(utilizada);
        if (novoTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new EstoqueNaoPodeSerNegativoException(String.format(
                    "Estoque insuficiente para finalizar a reserva de '%s'. Total atual: %s, utilizado: %s.",
                    produto.getNome(),
                    totalAtual.stripTrailingZeros().toPlainString(),
                    utilizada.stripTrailingZeros().toPlainString()
            ));
        }

        estoque.setReservado(novoReservado);
        estoque.setQuantidadeTotal(novoTotal);
        estoque.setQuantidadeDisponivel(novoTotal.subtract(novoReservado));

        Estoque salvo = repository.save(estoque);
        atualizarStatusProduto(produto, salvo.getQuantidadeDisponivel());

        if (utilizada.compareTo(BigDecimal.ZERO) > 0) {
            registrarHistorico(
                    salvo,
                    TipoMovimentacao.SAIDA,
                    null,
                    OrigemMovimentacao.AGENDAMENTO,
                    null,
                    utilizada,
                    produto,
                    "Baixa de estoque por conclusao de agendamento"
            );
        }
    }

    public Estoque buscarEstoquePorIdProduto(Produto produto) {
        Estoque estoque = repository.findByProduto(produto).orElseThrow(ProdutoNaoEncontradoException::new);
        sincronizarReservaComAgendamentosAtivos(estoque);
        return estoque;
    }

    public void sincronizarReservaPorProduto(Produto produto) {
        if (produto == null || produto.getId() == null) return;

        repository.findByProdutoId(produto.getId())
                .ifPresent(this::sincronizarReservaComAgendamentosAtivos);
    }

    public void validarReservaDetalheServico(Produto produto, BigDecimal quantidadeAdicional, Integer pedidoIdIgnorado) {
        if (produto == null || produto.getId() == null) {
            throw new IllegalArgumentException("Produto é obrigatório para validar reserva.");
        }

        if (quantidadeAdicional == null || quantidadeAdicional.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        Estoque estoque = repository.findByProdutoId(produto.getId())
                .orElseThrow(EstoqueNaoEncontradoException::new);

        BigDecimal totalAtual = estoque.getQuantidadeTotal() != null ? estoque.getQuantidadeTotal() : BigDecimal.ZERO;
        BigDecimal reservadoAgendamento = agendamentoProdutoRepository
                .somarReservasAtivasPorProdutoId(produto.getId());
        BigDecimal reservadoDetalheServico = itemPedidoRepository
                .somarReservasDetalheServicoAtivasPorProdutoId(produto.getId(), pedidoIdIgnorado);

        if (reservadoAgendamento == null) reservadoAgendamento = BigDecimal.ZERO;
        if (reservadoDetalheServico == null) reservadoDetalheServico = BigDecimal.ZERO;

        BigDecimal totalReservado = reservadoAgendamento
                .add(reservadoDetalheServico)
                .add(quantidadeAdicional);

        if (totalReservado.compareTo(totalAtual) > 0) {
            BigDecimal disponivelParaReserva = totalAtual.subtract(reservadoAgendamento.add(reservadoDetalheServico));
            if (disponivelParaReserva.compareTo(BigDecimal.ZERO) < 0) {
                disponivelParaReserva = BigDecimal.ZERO;
            }

            throw new EstoqueNaoPodeSerNegativoException(String.format(
                    "Estoque insuficiente para reservar '%s'. Disponivel para reserva: %s, solicitado adicionalmente: %s.",
                    produto.getNome(),
                    disponivelParaReserva.stripTrailingZeros().toPlainString(),
                    quantidadeAdicional.stripTrailingZeros().toPlainString()
            ));
        }
    }

    private void sincronizarReservaComAgendamentosAtivos(Estoque estoque) {
        if (estoque == null || estoque.getProduto() == null || estoque.getProduto().getId() == null) return;

        BigDecimal totalAtual = estoque.getQuantidadeTotal() != null ? estoque.getQuantidadeTotal() : BigDecimal.ZERO;
        BigDecimal reservadoAtual = estoque.getReservado() != null ? estoque.getReservado() : BigDecimal.ZERO;
        BigDecimal reservadoAgendamento = agendamentoProdutoRepository
                .somarReservasAtivasPorProdutoId(estoque.getProduto().getId());
        BigDecimal reservadoDetalheServico = itemPedidoRepository
                .somarReservasDetalheServicoAtivasPorProdutoId(estoque.getProduto().getId(), null);

        if (reservadoAgendamento == null) reservadoAgendamento = BigDecimal.ZERO;
        if (reservadoDetalheServico == null) reservadoDetalheServico = BigDecimal.ZERO;

        BigDecimal reservadoAtivo = reservadoAgendamento.add(reservadoDetalheServico);
        if (reservadoAtivo.compareTo(totalAtual) > 0) reservadoAtivo = totalAtual;

        if (reservadoAtual.compareTo(reservadoAtivo) != 0) {
            estoque.setReservado(reservadoAtivo);
            estoque.setQuantidadeDisponivel(totalAtual.subtract(reservadoAtivo));
            repository.save(estoque);
        } else if (estoque.getQuantidadeDisponivel() == null
                || estoque.getQuantidadeDisponivel().compareTo(totalAtual.subtract(reservadoAtivo)) != 0) {
            estoque.setQuantidadeDisponivel(totalAtual.subtract(reservadoAtivo));
            repository.save(estoque);
        }
    }
}
