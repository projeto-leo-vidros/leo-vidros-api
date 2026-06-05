package com.project.extension.service;

import com.project.extension.config.CacheConfig;
import com.project.extension.entity.Etapa;
import com.project.extension.entity.Pedido;
import com.project.extension.entity.Servico;
import com.project.extension.exception.naoencontrado.PedidoNaoEncontradoException;
import com.project.extension.repository.HistoricoEstoqueRepository;
import com.project.extension.repository.OrcamentoRepository;
import com.project.extension.repository.PedidoRepository;
import com.project.extension.strategy.pedido.PedidoContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class PedidoService {

    private final PedidoRepository repository;
    private final EtapaService etapaService;
    private final PedidoContext pedidoContext;
    private final AgendamentoService agendamentoService;
    private final HistoricoEstoqueRepository historicoEstoqueRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final EstoqueService estoqueService;
    private final LogService logService;

    @Transactional
    @CacheEvict(cacheNames = {CacheConfig.FATURAMENTO_MES, CacheConfig.FATURAMENTO_ANUAL}, allEntries = true)
    public Pedido cadastrar(Pedido pedido) {

        Pedido processado = pedidoContext.criar(pedido);
        Pedido salvo = repository.save(processado);
        sincronizarReservasDetalheServico(salvo.getItensPedido());

        logService.success(String.format(
                "Novo Pedido ID %d criado com sucesso. Tipo: %s, Total: %.2f.",
                salvo.getId(),
                salvo.getTipoPedido(),
                salvo.getValorTotal()
        ));

        return salvo;
    }

    public Pedido buscarPorId(Integer id) {
        return repository.findById(id).orElseThrow(() -> {
            String msg = String.format("Pedido ID %d não encontrado.", id);
            logService.error(msg);
            return new PedidoNaoEncontradoException();
        });
    }

    public Page<Pedido> listar(Pageable pageable) {
        Page<Pedido> pedidos = repository.findAll(pageable);
        logService.info(String.format("Listagem de pedidos: %d registros.", pedidos.getTotalElements()));
        return pedidos;
    }

    public Page<Pedido> listarPedidosPorTipoENomeDaEtapa(String nome, Pageable pageable) {
        Etapa etapa = etapaService.buscarPorTipoAndEtapa("PEDIDO", nome);
        Page<Pedido> pedidos = repository.findAllByServico_Etapa(etapa, pageable);
        log.info("Total de pedidos encontrados: {} para etapa: {}", pedidos.getTotalElements(), etapa.getNome());
        return pedidos;
    }

    @Transactional
    @CacheEvict(cacheNames = {CacheConfig.FATURAMENTO_MES, CacheConfig.FATURAMENTO_ANUAL}, allEntries = true)
    public Pedido editar(Integer id, Pedido pedidoAtualizar) {
        Pedido pedidoAntigo = buscarPorId(id);
        log.debug(String.valueOf(pedidoAntigo.getId()));
        pedidoAntigo.setId(id);
        Pedido processado = pedidoContext.editar(pedidoAntigo, pedidoAtualizar);

        Pedido salvo = repository.save(processado);
        sincronizarReservasDetalheServico(salvo.getItensPedido());

        logService.info(String.format(
                "Pedido ID %d atualizado com sucesso. Total: %.2f.",
                salvo.getId(),
                salvo.getValorTotal()
        ));

        return salvo;
    }

    @Transactional
    @CacheEvict(cacheNames = {CacheConfig.FATURAMENTO_MES, CacheConfig.FATURAMENTO_ANUAL}, allEntries = true)
    public void deletar(Integer id) {

        Pedido pedido = buscarPorId(id);
        List<com.project.extension.entity.Produto> produtosParaSincronizar = coletarProdutosDosItens(pedido.getItensPedido());
        Servico servico = pedido.getServico();

        if (servico != null && servico.getAgendamentos() != null) {
            var agendamentos = new ArrayList<>(servico.getAgendamentos());
            for (var agendamento : agendamentos) {
                agendamentoService.deletar(agendamento.getId());
            }
            servico.getAgendamentos().clear();
        }

        pedidoContext.deletar(pedido);
        orcamentoRepository.deleteByPedidoId(pedido.getId());
        historicoEstoqueRepository.deleteByPedidoId(pedido.getId());
        repository.delete(pedido);
        produtosParaSincronizar.forEach(estoqueService::sincronizarReservaPorProduto);

        logService.info(String.format(
                "Pedido ID %d excluído com sucesso.",
                id
        ));
    }

    public Page<Pedido> listarPedidosPorTipo(String tipo, Pageable pageable) {
        return repository.findByTipoPedidoIgnoreCase(tipo, pageable);
    }

    public Page<Pedido> listarPedidosDeServico(Pageable pageable) {
        return repository.findByServicoIsNotNull(pageable);
    }

    public Page<Pedido> listarPedidosDeProduto(Pageable pageable) {
        return repository.findByTipoPedidoIgnoreCaseAndItensPedidoIsNotEmpty("produto", pageable);
    }

    private void sincronizarReservasDetalheServico(List<com.project.extension.entity.ItemPedido> itensPedido) {
        coletarProdutosDosItens(itensPedido).forEach(estoqueService::sincronizarReservaPorProduto);
    }

    private List<com.project.extension.entity.Produto> coletarProdutosDosItens(List<com.project.extension.entity.ItemPedido> itensPedido) {
        Map<Integer, com.project.extension.entity.Produto> produtos = new LinkedHashMap<>();

        if (itensPedido == null) {
            return new ArrayList<>();
        }

        for (com.project.extension.entity.ItemPedido item : itensPedido) {
            if (item == null || item.getEstoque() == null || item.getEstoque().getProduto() == null) {
                continue;
            }

            com.project.extension.entity.Produto produto = item.getEstoque().getProduto();
            if (produto.getId() != null) {
                produtos.put(produto.getId(), produto);
            }
        }

        return new ArrayList<>(produtos.values());
    }
}
