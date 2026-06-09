package com.project.extension.service;

import com.project.extension.entity.AtributoProduto;
import com.project.extension.entity.Produto;
import com.project.extension.exception.naoencontrado.AtributoProdutoNaoEncontradoException;
import com.project.extension.repository.AtributoProdutoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class AtributoProdutoService {

    private final AtributoProdutoRepository repository;

    public AtributoProduto cadastrar(AtributoProduto atributoProduto, Produto produto) {
        atributoProduto.setProduto(produto);
        return repository.save(atributoProduto);
    }

    public AtributoProduto buscarPorId(Integer id) {
        return repository.findById(id).orElseThrow(() -> {
            log.warn("Atributo Produto com ID {} não encontrado", id);
            return new AtributoProdutoNaoEncontradoException();
        });
    }

    public List<AtributoProduto> listar() {
        return repository.findAll();
    }

    public AtributoProduto editar(AtributoProduto origem, Integer id) {
        AtributoProduto destino = this.buscarPorId(id);
        this.atualizarDadosBasicos(destino, origem);
        return repository.save(destino);
    }

    public void deletar(Integer id) {
        AtributoProduto atributo = this.buscarPorId(id);
        repository.deleteById(id);
    }

    private void atualizarDadosBasicos(AtributoProduto destino, AtributoProduto origem) {
        destino.setTipo(origem.getTipo());
        destino.setValor(origem.getValor());
    }
}
