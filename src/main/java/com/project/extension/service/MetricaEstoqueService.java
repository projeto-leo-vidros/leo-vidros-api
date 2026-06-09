package com.project.extension.service;

import com.project.extension.entity.MetricaEstoque;
import com.project.extension.exception.naoencontrado.MetricaNaoEncontradaException;
import com.project.extension.repository.MetricaEstoqueRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class MetricaEstoqueService {

    private final MetricaEstoqueRepository repository;

    public MetricaEstoque cadastrar(MetricaEstoque metricaEstoque) {
        return repository.save(metricaEstoque);
    }

    public MetricaEstoque buscarPorId(Integer id) {
        return repository.findById(id).orElseThrow(() -> {
            log.error("Métrica de estoque com ID {} não encontrado", id);
            return new MetricaNaoEncontradaException();
        });
    }

    public List<MetricaEstoque> listar() {
        return repository.findAll();
    }

    public MetricaEstoque editar(MetricaEstoque origem, Integer id) {
        MetricaEstoque destino = this.buscarPorId(id);
        this.atualizarDadosBasicos(destino, origem);
        return repository.save(destino);
    }

    public void deletar(Integer id) {
        repository.deleteById(id);
    }

    private void atualizarDadosBasicos(MetricaEstoque destino, MetricaEstoque origem) {
        destino.setNivelMinimo(origem.getNivelMinimo());
        destino.setNivelMaximo(origem.getNivelMaximo());
    }
}
