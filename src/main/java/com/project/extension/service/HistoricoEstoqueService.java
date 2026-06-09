package com.project.extension.service;

import com.project.extension.entity.HistoricoEstoque;
import com.project.extension.exception.naoencontrado.HistoricoEstoqueNaoEncontradoException;
import com.project.extension.repository.HistoricoEstoqueRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class HistoricoEstoqueService {
    private final HistoricoEstoqueRepository repository;

    public HistoricoEstoque cadastrar(HistoricoEstoque historicoEstoque){
        if (historicoEstoque.getUsuario() == null || historicoEstoque.getEstoque() == null) {
            throw new IllegalArgumentException("Usuário e Estoque são obrigatórios para registrar histórico.");
        }
        return repository.save(historicoEstoque);
    }

    public Page<HistoricoEstoque> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<HistoricoEstoque> buscarPorEstoqueId(Integer estoqueId, Pageable pageable) {
        Page<HistoricoEstoque> historicos = repository.findByEstoqueId(estoqueId, pageable);

        if (historicos.isEmpty()) {
            throw new HistoricoEstoqueNaoEncontradoException();
        }

        log.warn("Pedidos encontrados no histórico da página {}: {} registros", pageable.getPageNumber(), historicos.getNumberOfElements());
        return historicos;
    }
}
