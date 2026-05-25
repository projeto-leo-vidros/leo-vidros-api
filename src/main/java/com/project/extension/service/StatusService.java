package com.project.extension.service;

import com.project.extension.entity.Status;
import com.project.extension.exception.naoencontrado.StatusNaoEncontradoException;
import com.project.extension.repository.StatusRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class StatusService {
    private final StatusRepository repository;
    private final LogService logService;

    public Status cadastrar(Status status) {
        Status salvo = repository.save(status);
        String mensagem = String.format("Novo Status cadastrado com sucesso. ID: %d, Tipo: '%s', Nome: '%s'.",
                salvo.getId(), salvo.getTipo(), salvo.getNome());
        logService.success(mensagem);

        return salvo;
    }

    public Status buscarPorTipoAndStatus(String tipo, String nome) {
        return repository.findFirstByTipoAndNome(tipo, nome).orElseThrow(() -> {
            String mensagem = String.format("Falha na busca: Status do tipo '%s' e nome '%s' não encontrado.", tipo, nome);
            logService.error(mensagem);
            log.error(mensagem);
            return new StatusNaoEncontradoException();
        });
    }

    public Status buscarOuCriarPorTipoENome(String tipo, String nome) {
        return repository.findFirstByTipoAndNome(tipo, nome)
                .orElseGet(() -> {
                    log.warn("Status não encontrado (tipo='{}', nome='{}'). Criando novo...", tipo, nome);
                    Status novo = new Status();
                    novo.setTipo(tipo);
                    novo.setNome(nome);
                    Status salvo = repository.save(novo);
                    String mensagem = String.format("Novo Status criado implicitamente. ID: %d, Tipo: '%s', Nome: '%s'.",
                            salvo.getId(), salvo.getTipo(), salvo.getNome());
                    logService.warning(mensagem); // Usando WARNING para destacar a criação automática

                    return salvo;
                });
    }

}

