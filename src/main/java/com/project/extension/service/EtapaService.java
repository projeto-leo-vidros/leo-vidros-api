package com.project.extension.service;

import com.project.extension.entity.Etapa;
import com.project.extension.exception.naoencontrado.EtapaNaoEncontradoException;
import com.project.extension.repository.EtapaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class EtapaService {
    private final EtapaRepository repository;
    private final LogService logService;

    public Etapa cadastrar(Etapa etapa) {
        Etapa salvo = repository.save(etapa);
        String mensagem = String.format("Nova Etapa cadastrada com sucesso. ID: %d, Tipo: '%s', Nome: '%s'.",
                salvo.getId(), salvo.getTipo(), salvo.getNome());
        logService.success(mensagem); // Usando SUCCESS para indicar criação bem-sucedida

        return salvo;
    }

    public Etapa buscarOuCriarPorTipoENome(String tipo, String nome) {
        try {
            return buscarPorTipoAndEtapa(tipo, nome);
        } catch (EtapaNaoEncontradoException e) {
            log.warn("Etapa não encontrada (tipo='{}', nome='{}'). Criando nova...", tipo, nome);
            Etapa nova = new Etapa(tipo, nome);
            Etapa salvo = repository.save(nova);
            logService.warning(String.format(
                    "Nova Etapa criada implicitamente. ID: %d, Tipo: '%s', Nome: '%s'.",
                    salvo.getId(), salvo.getTipo(), salvo.getNome()));
            return salvo;
        }
    }

    public Etapa buscarPorTipoAndEtapa(String tipo, String nome) {
        var etapaExata = repository.findFirstByTipoAndNome(tipo, nome);
        if (etapaExata.isPresent()) {
            return etapaExata.get();
        }

        String nomeNormalizado = normalizar(nome);
        List<Etapa> todasDoTipo = repository.findByTipo(tipo);

        var etapaNormalizada = todasDoTipo.stream()
                .filter(e -> normalizar(e.getNome()).equals(nomeNormalizado))
                .findFirst();

        if (etapaNormalizada.isPresent()) {
            return etapaNormalizada.get();
        }

        var etapaParcial = todasDoTipo.stream()
                .filter(e -> {
                    String en = normalizar(e.getNome());
                    return en.contains(nomeNormalizado) || nomeNormalizado.contains(en);
                })
                .findFirst();

        if (etapaParcial.isPresent()) {
            return etapaParcial.get();
        }

        String mensagem = String.format("Falha na busca: Etapa do tipo '%s' e nome '%s' não encontrada.", tipo, nome);
        logService.error(mensagem);
        log.warn(mensagem);
        throw new EtapaNaoEncontradoException();
    }

    private String normalizar(String texto) {
        if (texto == null) return "";
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase()
                .trim();
    }
}
