package com.project.extension.service;

import com.project.extension.entity.AtributoProduto;
import com.project.extension.entity.MetricaEstoque;
import com.project.extension.entity.Produto;
import com.project.extension.exception.naoencontrado.ProdutoNaoEncontradoException;
import com.project.extension.repository.ProdutoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
public class ProdutoService {
    private final ProdutoRepository repository;
    private final AtributoProdutoService atributoProdutoService;
    private final MetricaEstoqueService metricaEstoqueService;
    private final LogService logService;

    public Produto cadastrar(Produto produto) {
        if (produto.getAtributos() != null) {
            for (AtributoProduto atributo : produto.getAtributos()) {
                atributo.setProduto(produto);
            }
        }

        if (produto.getMetricaEstoque() != null) {
            produto.setMetricaEstoque(metricaEstoqueService.cadastrar(produto.getMetricaEstoque()));
        }

        Produto produtoSalvo = repository.save(produto);
        String acao = produto.getId() == null ? "cadastrado" : "atualizado";
        String mensagem = String.format("Produto ID %d %s com sucesso. Nome: %s, Preço: %.2f.",
                produtoSalvo.getId(), acao, produtoSalvo.getNome(), produtoSalvo.getPreco());
        logService.success(mensagem);

        if (produto.getAtributos() != null) {
            for (AtributoProduto atributo : produto.getAtributos()) {
                atributoProdutoService.cadastrar(atributo, produtoSalvo);
            }
        }

        return produtoSalvo;
    }

    public Produto buscarPorId(Integer id) {
        return repository.findById(id).orElseThrow(() -> {
            log.warn("Falha na busca: Produto com ID {} não encontrado.", id);
            return new ProdutoNaoEncontradoException();
        });
    }

    public Page<Produto> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    public Produto editar(Produto origem, Integer id) {
        Produto destino = this.buscarPorId(id);

        this.atualizarDadosBasicos(destino, origem);
        this.atualizarAtributosProduto(destino, origem);
        this.atualizarMetricaEstoque(destino, origem);

        return this.cadastrar(destino);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletar(Integer id) {
        Produto produto = this.buscarPorId(id);

        if (produto.getAtributos() != null) {
            for (AtributoProduto atributo : produto.getAtributos()) {
                atributoProdutoService.deletar(atributo.getId());
            }
        }

        if (produto.getMetricaEstoque() != null) {
            metricaEstoqueService.deletar(produto.getMetricaEstoque().getId());
            produto.setMetricaEstoque(null);
        }

        repository.delete(produto);
    }

    private void atualizarDadosBasicos(Produto destino, Produto origem) {
        destino.setNome(origem.getNome());
        destino.setDescricao(origem.getDescricao());
        destino.setUnidademedida(origem.getUnidademedida());
        destino.setPreco(origem.getPreco());
        destino.setAtivo(origem.getAtivo());
    }

    private void atualizarAtributosProduto(Produto produtoDestino, Produto produtoOrigem) {
        if (produtoOrigem.getAtributos() == null) return;

        if (produtoDestino.getAtributos() == null) {
            produtoDestino.setAtributos(new ArrayList<>());
        }

        Map<Integer, AtributoProduto> atributosAtuais = produtoDestino.getAtributos().stream()
                .filter(attr -> attr.getId() != null)
                .collect(Collectors.toMap(AtributoProduto::getId, attr -> attr));

        List<AtributoProduto> atributosAtualizados = new ArrayList<>();

        for (AtributoProduto atributoOrigem : produtoOrigem.getAtributos()) {
            atributoOrigem.setProduto(produtoDestino);

            if (atributoOrigem.getId() != null && atributosAtuais.containsKey(atributoOrigem.getId())) {
                AtributoProduto attrAtualizado = atributoProdutoService.editar(atributoOrigem, atributoOrigem.getId());
                atributosAtualizados.add(attrAtualizado);
                atributosAtuais.remove(atributoOrigem.getId());
            } else {
                AtributoProduto attrNovo = atributoProdutoService.cadastrar(atributoOrigem, produtoDestino);
                atributosAtualizados.add(attrNovo);
            }
        }

        for (AtributoProduto attrRemover : atributosAtuais.values()) {
            atributoProdutoService.deletar(attrRemover.getId());
        }
        produtoDestino.setAtributos(atributosAtualizados);
    }
    public void atualizarMetricaEstoque(Produto produtoDestino, Produto produtoOrigem) {
        if (produtoOrigem.getMetricaEstoque() == null) return;

        if (produtoDestino.getMetricaEstoque() != null &&
                produtoDestino.getMetricaEstoque().getId() != null) {

            Integer id = produtoDestino.getMetricaEstoque().getId();
            MetricaEstoque metricaAtualizada = metricaEstoqueService.editar(
                    produtoOrigem.getMetricaEstoque(),
                    id
            );

            produtoDestino.setMetricaEstoque(metricaAtualizada);
            return;
        }

        MetricaEstoque novaMetrica = metricaEstoqueService.cadastrar(
                produtoOrigem.getMetricaEstoque()
        );

        produtoDestino.setMetricaEstoque(novaMetrica);
    }

    public Produto atualizarStatus(Integer id, String status) {
        Produto produtoAtualizar = this.buscarPorId(id);
        boolean novoStatus = Boolean.parseBoolean(status);
        produtoAtualizar.setAtivo(novoStatus);
        return repository.save(produtoAtualizar);
    }
}
