package com.project.extension.service;

import com.project.extension.entity.AgendamentoProduto;
import com.project.extension.exception.naoencontrado.AgendamentoProdutoNaoEncontradoException;
import com.project.extension.repository.AgendamentoProdutoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class AgendamentoProdutoService {
    private final AgendamentoProdutoRepository repository;

    public AgendamentoProduto cadastrar(AgendamentoProduto agendamentoProduto) {
        return repository.save(agendamentoProduto);
    }

    public AgendamentoProduto buscarPorId(Integer id) {
        return repository.findById(id).orElseThrow(() -> {
            String mensagem = String.format("AgendamentoProduto com ID %d não encontrado durante busca.", id);
            log.warn(mensagem);
            return new AgendamentoProdutoNaoEncontradoException();
        });
    }

    public AgendamentoProduto editar(AgendamentoProduto atualizacao, Integer id) {
        AgendamentoProduto existente = this.buscarPorId(id);

        if (atualizacao.getQuantidadeReservada() != null) {
            existente.setQuantidadeReservada(atualizacao.getQuantidadeReservada());
        }

        if (atualizacao.getQuantidadeUtilizada() != null) {
            existente.setQuantidadeUtilizada(atualizacao.getQuantidadeUtilizada());
        }

        return repository.save(existente);
    }

    public void deletar(Integer id) {
        AgendamentoProduto existente = this.buscarPorId(id);
        repository.delete(existente);
    }
}
