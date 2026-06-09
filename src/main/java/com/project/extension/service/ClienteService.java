package com.project.extension.service;

import com.project.extension.entity.*;
import com.project.extension.exception.naoencontrado.ClienteNaoEncontradoException;
import com.project.extension.repository.ClienteRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
public class ClienteService {
    private final ClienteRepository repository;
    private final EnderecoService enderecoService;
    private final StatusService statusService;
    private final LogService logService;

    public Cliente cadastrar(Cliente cliente){
        Cliente novoCliente = repository.save(cliente);
        String mensagem = String.format("Novo Cliente ID %d cadastrado com sucesso. Nome: %s.",
                novoCliente.getId(),
                novoCliente.getNome());
        logService.success(mensagem);
        return novoCliente;
    }

    public Cliente buscarPorId(Integer id) {
        return repository.findById(id).orElseThrow(() -> {
            String mensagem = String.format("Falha na busca: Cliente com ID %d não encontrado.", id);
            log.warn(mensagem);
            return new ClienteNaoEncontradoException();
        });
    }

    public Page<Cliente> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    public Cliente atualizar(Cliente origem, Integer id){
       Cliente destino = this.buscarPorId(id);

       atualizarDadosBasicos(destino, origem);
       atualizarEnderecos(destino, origem);
       atualizarStatus(destino, origem);

       return repository.save(destino);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletar(Integer id){
        Cliente clienteParaDeletar = this.buscarPorId(id);
        repository.deleteById(id);
    }

    private void atualizarDadosBasicos(Cliente destino, Cliente origem) {
        destino.setNome(origem.getNome());
        destino.setCpf(origem.getCpf());
        destino.setEmail(origem.getEmail());
        destino.setTelefone(origem.getTelefone());
    }

    private void atualizarEnderecos(Cliente destino, Cliente origem) {
        if (origem.getEnderecos() != null) {
            List<Endereco> enderecosAtualizados = origem.getEnderecos().stream()
                    .map(endereco -> {
                        if (endereco.getId() != null) {
                            return enderecoService.editar(endereco, endereco.getId());
                        } else {
                            return enderecoService.cadastrar(endereco);
                        }
                    })
                    .collect(Collectors.toList());

            destino.setEnderecos(enderecosAtualizados);
        }
    }

    private void atualizarStatus(Cliente destino, Cliente origem) {
        String origemStatus = origem.getStatus();
        if (origemStatus == null) return;

        String destinoStatus = destino.getStatus();
        if (destinoStatus == null || !destinoStatus.equals(origemStatus)) {
            logService.warning(String.format(
                    "Status do Cliente ID %d alterado de %s para %s.",
                    destino.getId(),
                    destinoStatus,
                    origemStatus
            ));
        }

        destino.setStatus(origemStatus);
    }

}
