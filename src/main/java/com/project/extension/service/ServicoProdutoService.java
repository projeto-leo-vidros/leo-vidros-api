package com.project.extension.service;

import com.project.extension.controller.servicoproduto.dto.ServicoProdutoRequestDto;
import com.project.extension.entity.Produto;
import com.project.extension.entity.Servico;
import com.project.extension.entity.ServicoProduto;
import com.project.extension.exception.RegraNegocioException;
import com.project.extension.repository.ServicoProdutoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Fonte única de verdade da lista de produtos de um pedido de serviço.
 * Toda escrita relevante (detalhe, orçamento, conclusão) passa por aqui.
 */
@Service
@Slf4j
@AllArgsConstructor
public class ServicoProdutoService {

    private final ServicoProdutoRepository repository;
    private final ServicoService servicoService;
    private final ProdutoService produtoService;
    private final LogService logService;

    public List<ServicoProduto> listarPorServico(Integer servicoId) {
        return repository.findByServicoIdAndAtivoTrueOrderByOrdemAscIdAsc(servicoId);
    }

    /**
     * Recebe a lista completa e faz o diff: insere novos, atualiza quantidade/preço/observação
     * dos existentes e desativa (soft-delete) os que sumiram da lista.
     */
    @Transactional
    public List<ServicoProduto> substituirLista(Integer servicoId, List<ServicoProdutoRequestDto> itens) {
        Servico servico = servicoService.buscarPorId(servicoId);
        boolean execucaoOuConcluido = etapaBloqueiaRemocao(servico);

        List<ServicoProduto> existentes = repository.findByServicoIdOrderByOrdemAscIdAsc(servicoId);
        List<ServicoProdutoRequestDto> requisitados = itens != null ? itens : new ArrayList<>();

        Set<Integer> produtosRequisitados = new HashSet<>();
        int ordem = 0;

        for (ServicoProdutoRequestDto dto : requisitados) {
            if (dto == null || dto.produtoId() == null) continue;
            produtosRequisitados.add(dto.produtoId());

            Produto produto = produtoService.buscarPorId(dto.produtoId());
            ServicoProduto registro = existentes.stream()
                    .filter(sp -> sp.getProduto() != null && dto.produtoId().equals(sp.getProduto().getId()))
                    .findFirst()
                    .orElseGet(() -> novoRegistro(servico, produto));

            registro.setQuantidadePlanejada(dto.quantidadePlanejada() != null ? dto.quantidadePlanejada() : BigDecimal.ZERO);
            if (dto.precoUnitario() != null) {
                registro.setPrecoUnitario(dto.precoUnitario());
            }
            registro.setObservacao(dto.observacao());
            registro.setOrdem(dto.ordem() != null ? dto.ordem() : ordem);
            registro.setAtivo(true);
            repository.save(registro);
            ordem++;
        }

        // Soft-delete dos que saíram da lista.
        for (ServicoProduto existente : existentes) {
            Integer produtoId = existente.getProduto() != null ? existente.getProduto().getId() : null;
            if (produtoId == null || produtosRequisitados.contains(produtoId)) continue;
            if (!Boolean.TRUE.equals(existente.getAtivo())) continue;

            if (execucaoOuConcluido) {
                throw new RegraNegocioException(
                        "Não é permitido remover produtos da lista após o serviço estar em execução ou concluído.");
            }
            existente.setAtivo(false);
            repository.save(existente);
        }

        logService.info(String.format("Lista de produtos do Serviço ID %d substituída (%d itens ativos).",
                servicoId, produtosRequisitados.size()));

        return listarPorServico(servicoId);
    }

    /**
     * Sincronização reversa a partir do orçamento: itens do orçamento (incluindo extras)
     * passam a ser a fonte de verdade da seção do detalhe.
     */
    @Transactional
    public void adicionarOuAtualizar(Integer servicoId, Integer produtoId, BigDecimal quantidade,
                                     BigDecimal precoUnitario, String observacao, Integer ordem) {
        if (produtoId == null) return;

        Servico servico = servicoService.buscarPorId(servicoId);
        Produto produto = produtoService.buscarPorId(produtoId);

        ServicoProduto registro = repository.findByServicoIdAndProdutoId(servicoId, produtoId)
                .orElseGet(() -> novoRegistro(servico, produto));

        registro.setQuantidadePlanejada(quantidade != null ? quantidade : BigDecimal.ZERO);
        if (precoUnitario != null) {
            registro.setPrecoUnitario(precoUnitario);
        }
        registro.setObservacao(observacao);
        if (ordem != null) {
            registro.setOrdem(ordem);
        }
        registro.setAtivo(true);
        repository.save(registro);
    }

    /**
     * Substitui completamente a lista ativa do serviço a partir de uma sincronização externa
     * (ex.: orçamento). Produtos ausentes são desativados.
     */
    @Transactional
    public void sincronizarComProdutos(Integer servicoId, List<SyncItem> itens) {
        List<ServicoProduto> existentes = repository.findByServicoIdOrderByOrdemAscIdAsc(servicoId);
        Set<Integer> presentes = new HashSet<>();

        int ordem = 0;
        for (SyncItem item : itens) {
            if (item == null || item.produtoId() == null) continue;
            presentes.add(item.produtoId());
            adicionarOuAtualizar(servicoId, item.produtoId(), item.quantidade(),
                    item.precoUnitario(), item.observacao(), ordem++);
        }

        for (ServicoProduto existente : existentes) {
            Integer produtoId = existente.getProduto() != null ? existente.getProduto().getId() : null;
            if (produtoId == null || presentes.contains(produtoId)) continue;
            if (!Boolean.TRUE.equals(existente.getAtivo())) continue;
            existente.setAtivo(false);
            repository.save(existente);
        }
    }

    /**
     * Grava a quantidade efetivamente utilizada de um produto (fluxo de conclusão de agendamento).
     */
    @Transactional
    public void marcarUtilizacao(Integer servicoId, Integer produtoId, BigDecimal quantidadeUtilizada) {
        repository.findByServicoIdAndProdutoId(servicoId, produtoId).ifPresent(sp -> {
            sp.setQuantidadeUtilizada(quantidadeUtilizada);
            repository.save(sp);
        });
    }

    private ServicoProduto novoRegistro(Servico servico, Produto produto) {
        ServicoProduto sp = new ServicoProduto();
        sp.setServico(servico);
        sp.setProduto(produto);
        sp.setQuantidadePlanejada(BigDecimal.ZERO);
        sp.setPrecoUnitario(produto.getPreco() != null ? BigDecimal.valueOf(produto.getPreco()) : BigDecimal.ZERO);
        sp.setAtivo(true);
        return sp;
    }

    private boolean etapaBloqueiaRemocao(Servico servico) {
        if (servico == null || servico.getEtapa() == null) return false;
        String etapa = normalizar(servico.getEtapa().getNome());
        return etapa.contains("EXECUCAO") || etapa.equals("CONCLUIDO");
    }

    private String normalizar(String valor) {
        if (valor == null) return "";
        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('_', ' ')
                .trim()
                .toUpperCase();
    }

    public record SyncItem(Integer produtoId, BigDecimal quantidade, BigDecimal precoUnitario, String observacao) {}
}
