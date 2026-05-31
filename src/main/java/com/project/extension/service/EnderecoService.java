package com.project.extension.service;

import com.project.extension.entity.Endereco;
import com.project.extension.exception.naoencontrado.EnderecoNaoEncontradoException;
import com.project.extension.repository.EnderecoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
public class EnderecoService {

    private final EnderecoRepository repository;
    private final LogService logService;

    public Endereco cadastrar(Endereco endereco) {
        return repository.save(endereco);
    }

    public Endereco buscarPorId(Integer id) {
        return repository.findById(id).orElseThrow(() -> {
            log.warn("Endereço com ID {} não encontrado", id);
            return new EnderecoNaoEncontradoException();
        });
    }

    public Endereco buscarPorCep(String cep) {
        List<Endereco> enderecos = repository.findAllByCepOrderByIdDesc(cep);
        if (enderecos == null || enderecos.isEmpty()) {
            return null;
        }

        if (enderecos.size() > 1) {
            logService.warning(String.format(
                    "Foram encontrados %d endereços para o CEP %s. Utilizando o mais recente (ID %d).",
                    enderecos.size(),
                    cep,
                    enderecos.get(0).getId()
            ));
        }

        return enderecos.get(0);
    }

    public List<Endereco> listar() {
        return repository.findAll();
    }

    private void atualizarCampos(Endereco destino, Endereco origem) {
        destino.setRua(origem.getRua());
        destino.setCep(origem.getCep());
        destino.setBairro(origem.getBairro());
        destino.setCidade(origem.getCidade());
        destino.setComplemento(origem.getComplemento());
        destino.setPais(origem.getPais());
        destino.setUf(origem.getUf());
        destino.setNumero(origem.getNumero());
    }

    @Transactional(rollbackFor = Exception.class)
    public Endereco editar(Endereco origem, Integer id) {
        Endereco destino = this.buscarPorId(id);
        atualizarCampos(destino, origem);
        return repository.save(destino);
    }

    public void deletar(Integer id) {
        this.buscarPorId(id);
        repository.deleteById(id);
    }
}
